package com.getcollate.expenseSplitter.repository;

import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.trip.Trip;
import com.getcollate.trip.accounts.Transaction;

import java.util.List;

public interface TripTransactionReporitory {
    List<Transaction> getAllTransactions(String tripId);
    Transaction getTransaction(String tripId, String transactionId);
    void deleteTransaction(String tripId, String transactionId);
    boolean createTransaction(String tripId, List<Transaction> transaction);
    Trip getTripForTransaction(String tripId) throws ValidationException;
}
