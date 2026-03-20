package com.split.expenseSplitter.service.impl;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.pojo.PostTransactionRequest;
import com.split.expenseSplitter.repository.TripTransactionReporitory;
import com.split.expenseSplitter.service.TripTransactionService;
import com.split.trip.Trip;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class TripTransactionServiceImpl implements TripTransactionService {

    TripTransactionReporitory tripTransactionReporitory;

    public TripTransactionServiceImpl(@Qualifier("postgresTripTransactionRepository") TripTransactionReporitory tripTransactionReporitory) {
        this.tripTransactionReporitory = tripTransactionReporitory;
    }

    @Override
    public List<Transaction> getTransactions(String tripId) {
        List<Transaction> list = tripTransactionReporitory.getAllTransactions(tripId);
        return list;
    }

    @Override
    public Transaction getTransaction(String tripId, String transactionId) {
        return tripTransactionReporitory.getTransaction(tripId, transactionId);
    }

    @Override
    public void deleteTransaction(String tripId, String transactionId) {
        tripTransactionReporitory.deleteTransaction(tripId, transactionId);
    }

    @Override
    public List<Transaction> createTransaction(String tripId, PostTransactionRequest transactionRequest) {
        Trip trip = tripTransactionReporitory.getTripForTransaction(tripId);
        List<Transaction> mappedTransactions = transactionRequest.getTransactions().stream().map(req -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    CATEGORY.valueOf(req.getSpentOn().toUpperCase());
                } catch (Exception e) {
                    throw new ValidationException("Invalid category: " + req.getSpentOn());
                }
                return new Transaction(
                        Float.valueOf(req.getSpentAmount()),
                        trip.getParticipant(req.getSpentBy()), // Wrapping the String ID into a Participant object
                        CATEGORY.valueOf(req.getSpentOn().toUpperCase()), // Converting String to Enum
                        SHARETYPE.EQUAL, // I defaulted the share type to be EQUAL... SHARETYPE SPONSORED also works!
                        dateFormat.parse(req.getSpentDate()), // Parsing the DD/MM/YYYY string
                        req.getBenefittedBy().stream()
                                .map(participantId -> trip.getParticipant(participantId))
                                .toList() // Converting List<String> to List<Participant>
                );
            } catch (ParseException e) {
                throw new ValidationException("Invalid date format for: " + req.getSpentDate());
            }
        }).toList();
        tripTransactionReporitory.createTransaction(tripId, mappedTransactions);
        return mappedTransactions;
    }
}
