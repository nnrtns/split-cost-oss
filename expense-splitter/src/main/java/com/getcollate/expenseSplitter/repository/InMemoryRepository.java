package com.getcollate.expenseSplitter.repository;

import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.trip.Trip;
import com.getcollate.trip.accounts.Transaction;
import com.getcollate.trip.accounts.settler.Debt;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/*
    acts like the in memory database. Utilised by the MemoryRepository
    This maintains the state of the application... therefore until we have a NOSQL/RDBMS Repository which speaks to an
    actual database, we will have concurrency issues since objects are shared. Inconsistancies will not be there since
    this repository class locks updates using the compute method of ComcurrentHashMap.
    This class is for temporary use to avoid the dependence on an actual database and not ment for production use.
 */
@Repository
public class InMemoryRepository implements TripRepository, SettlementRepository, TripTransactionReporitory {
    // trip id and trip object
    private final Map<String, Trip> trips = new ConcurrentHashMap<>();

    @Override
    public boolean createTrip(Trip trip) {
        Trip t = trips.computeIfAbsent(trip.getTripId(), id -> {
            Trip brandNewTrip = trip;
            return brandNewTrip;
        });
        return t != null ? true : false;
    }

    @Override
    public Trip updateTrip(String tripId, Trip trip) {
        trips.compute(tripId, (key, existingTrip) -> {
            if (existingTrip == null)
                throw new ValidationException("You are trying to update a trip that does not exist");

            existingTrip.setParticipants(trip.getParticipants());

            return existingTrip;
        });
        return trips.get(tripId);
    }

    @Override
    public void deleteTrip(String tripId) {
        trips.compute(tripId, (key, existingTrip) -> {
            if (existingTrip == null)
                throw new ValidationException("You are trying to delete a trip that does not exist");

            return null;
        });
    }

    @Override
    public List<Trip> getAllTrips() {
        return new ArrayList<>(trips.values());
    }

    @Override
    public Trip getTripById(String tripId) {
        Trip trip = trips.get(tripId);
        if (trip == null)
            throw new ValidationException("Trip not found");
        return trip;
    }

    // Settlement Repository
    @Override
    public boolean persistsettlement(String tripId, List<Debt> settlements) {
        trips.compute(tripId, ((key, existingTrip) -> {
            if (existingTrip == null)
                throw new ValidationException("You are trying to update a trip that does not exist");
            existingTrip.setSettlements(settlements);
            return existingTrip;
        }));
        return false;
    }

    @Override
    public List<Transaction> getAllTransactions(String tripId) {
        if (trips.get(tripId) == null)
            throw new ValidationException("Trip not found");
        List<Transaction> transactions = trips.get(tripId).getBalanceSheet().getTransactions();
        return transactions;
    }

    @Override
    public Transaction getTransaction(String tripId, String transactionId) {
        if (trips.get(tripId) == null)
            throw new ValidationException("Trip not found");
        List<Transaction> transactions = trips.get(tripId).getBalanceSheet().getTransactions();
        Stream<Transaction> transaction = transactions.stream().filter((t) -> t.transactionId() == transactionId);
        if (transaction.count() == 0)
            throw new ValidationException("Transaction not found");
        return transaction.findFirst().get();
    }

    @Override
    public void deleteTransaction(String tripId, String transactionId) {
        if (trips.get(tripId) == null)
            throw new ValidationException("Trip not found");
        Trip trip = trips.get(tripId);
        boolean r = trip.getBalanceSheet().getTransactions().removeIf(t -> t.transactionId().equals(transactionId));
        if (!r)
            throw new ValidationException("Transaction not found to be deleted");
    }

    @Override
    public boolean createTransaction(String tripId, List<Transaction> transaction) {
        if (trips.get(tripId) == null)
            throw new ValidationException("Trip not found");
        Trip trip = trips.get(tripId);
        List<Transaction> existingTransaction = trip.getBalanceSheet().getTransactions();
        List<Transaction> duplicates = transaction.stream().filter(t1 -> existingTransaction.stream().anyMatch(t2 -> t2.transactionId().equals(t1.transactionId()))).toList();
        if (!duplicates.isEmpty())
            throw new ValidationException("Duplicate transactions found: " + duplicates);
        existingTransaction.addAll(transaction);
        return true;
    }
}
