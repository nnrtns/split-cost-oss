package com.split.expenseSplitter.service;

import com.split.expenseSplitter.exception.DuplicateInsertionException;
import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.pojo.PostTransactionRequest;
import com.split.trip.accounts.Transaction;

import java.util.List;

public interface TripTransactionService {
    List<Transaction> getTransactions(String tripId)  throws ValidationException;
    Transaction getTransaction(String tripId, String transactionId)  throws ValidationException;
    void deleteTransaction(String tripId, String transactionId)  throws ValidationException;
    List<Transaction> createTransaction(String tripId, PostTransactionRequest transactionRequest)  throws ValidationException, DuplicateInsertionException;
}
