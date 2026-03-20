package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.postgres.jpa.TripCurrentSettlementJpaRepository;
import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.settler.Debt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresTripTransactionRepositoryIT extends AbstractPostgresRepositoryIT {

    @Autowired
    private PostgresTripRepository tripRepository;

    @Autowired
    private PostgresTripTransactionRepository transactionRepository;

    @Autowired
    private PostgresSettlementRepository settlementRepository;

    @Autowired
    private TripCurrentSettlementJpaRepository settlementJpaRepository;

    @Test
    void createTransaction_andReadBack_shouldRoundTripInOrder() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);

        Transaction tx1 = new Transaction("11111111-1111-1111-1111-111111111111", 100f, new Participant("Alice", "P1"), CATEGORY.FOOD, SHARETYPE.EQUAL, new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"), List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        Transaction tx2 = new Transaction("22222222-2222-2222-2222-222222222222", 50f, new Participant("Bob", "P2"), CATEGORY.TRANSPORT, SHARETYPE.EQUAL, new SimpleDateFormat("dd/MM/yyyy").parse("02/01/2020"), List.of(new Participant("Bob", "P2")));

        transactionRepository.createTransaction(trip.getTripId(), List.of(tx1, tx2));

        List<Transaction> loaded = transactionRepository.getAllTransactions(trip.getTripId());
        assertEquals(2, loaded.size());
        assertEquals("11111111-1111-1111-1111-111111111111", loaded.get(0).transactionId());
        assertEquals("22222222-2222-2222-2222-222222222222", loaded.get(1).transactionId());

        Transaction single = transactionRepository.getTransaction(trip.getTripId(), "22222222-2222-2222-2222-222222222222");
        assertEquals("22222222-2222-2222-2222-222222222222", single.transactionId());
        assertEquals("P2", single.spentBy().participantId());
    }

    @Test
    void createTransaction_shouldRejectMissingTrip() throws Exception {
        Transaction tx = new Transaction("11111111-1111-1111-1111-111111111111", 100f, new Participant("Alice", "P1"), CATEGORY.FOOD, SHARETYPE.EQUAL, new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"), List.of(new Participant("Alice", "P1")));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionRepository.createTransaction(UUID.randomUUID().toString(), List.of(tx)));

        assertEquals("Trip not found", ex.getMessage());
    }

    @Test
    void createTransaction_shouldRejectDuplicateTransactionIds() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        tripRepository.createTrip(trip);

        Transaction tx = new Transaction("11111111-1111-1111-1111-111111111111", 100f, new Participant("Alice", "P1"), CATEGORY.FOOD, SHARETYPE.EQUAL, new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"), List.of(new Participant("Alice", "P1")));
        transactionRepository.createTransaction(trip.getTripId(), List.of(tx));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionRepository.createTransaction(trip.getTripId(), List.of(tx)));

        assertTrue(ex.getMessage().contains("Duplicate transactions found"));
    }

    @Test
    void deleteTransaction_shouldDeleteTransactionAndClearPersistedSettlementSnapshot() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);

        Transaction tx = new Transaction("11111111-1111-1111-1111-111111111111", 100f, new Participant("Alice", "P1"), CATEGORY.FOOD, SHARETYPE.EQUAL, new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"), List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        transactionRepository.createTransaction(trip.getTripId(), List.of(tx));
        settlementRepository.persistsettlement(trip.getTripId(), List.of(new Debt("P2", "P1", 50f)));
        assertEquals(1, settlementJpaRepository.findByIdTripIdOrderByIdSettlementOrderAsc(UUID.fromString(trip.getTripId())).size());

        transactionRepository.deleteTransaction(trip.getTripId(), tx.transactionId());

        assertTrue(transactionRepository.getAllTransactions(trip.getTripId()).isEmpty());
        assertTrue(settlementJpaRepository.findByIdTripIdOrderByIdSettlementOrderAsc(UUID.fromString(trip.getTripId())).isEmpty());
    }

    @Test
    void getTransaction_shouldRejectMissingTransaction() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        tripRepository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionRepository.getTransaction(trip.getTripId(), "11111111-1111-1111-1111-111111111111"));

        assertEquals("Transaction not found", ex.getMessage());
    }

    @Test
    void deleteTransaction_shouldRejectMissingTransaction() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        tripRepository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionRepository.deleteTransaction(trip.getTripId(), "11111111-1111-1111-1111-111111111111"));

        assertEquals("Transaction not found to be deleted", ex.getMessage());
    }

    @Test
    void getTripForTransaction_shouldReturnActiveParticipantsOnly() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);
        tripRepository.removeParticipants(trip.getTripId(), List.of("P2"));

        Trip loaded = transactionRepository.getTripForTransaction(trip.getTripId());

        assertEquals(1, loaded.getParticipants().size());
        assertEquals("P1", loaded.getParticipants().getFirst().participantId());
    }
}
