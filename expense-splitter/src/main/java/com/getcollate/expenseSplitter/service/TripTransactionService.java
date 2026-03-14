package com.getcollate.expenseSplitter.service;

import com.getcollate.expenseSplitter.exception.DuplicateInsertionException;
import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.expenseSplitter.pojo.PostTransactionRequest;
import com.getcollate.trip.Trip;
import com.getcollate.trip.accounts.Transaction;

import java.util.List;

public interface TripTransactionService {
    List<Transaction> getTransactions(String tripId)  throws ValidationException;
    Transaction getTransaction(String tripId, String transactionId)  throws ValidationException;
    void deleteTransaction(String tripId, String transactionId)  throws ValidationException;
    List<Transaction> createTransaction(String tripId, PostTransactionRequest transactionRequest)  throws ValidationException, DuplicateInsertionException;
}
