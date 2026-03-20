package com.split.expenseSplitter.repository;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.trip.Trip;
import com.split.trip.accounts.Transaction;

import java.util.List;

public interface TripTransactionReporitory {
    List<Transaction> getAllTransactions(String tripId);
    Transaction getTransaction(String tripId, String transactionId);
    void deleteTransaction(String tripId, String transactionId);
    boolean createTransaction(String tripId, List<Transaction> transaction);
    Trip getTripForTransaction(String tripId) throws ValidationException;
}
