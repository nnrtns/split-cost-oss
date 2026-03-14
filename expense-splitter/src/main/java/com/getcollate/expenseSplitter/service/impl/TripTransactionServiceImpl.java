package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.pojo.PostTransactionRequest;
import com.getcollate.expenseSplitter.repository.TripTransactionReporitory;
import com.getcollate.expenseSplitter.service.TripTransactionService;
import com.getcollate.trip.accounts.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripTransactionServiceImpl implements TripTransactionService {

    TripTransactionReporitory tripTransactionReporitory;

    public TripTransactionServiceImpl(TripTransactionReporitory tripTransactionReporitory) {
        this.tripTransactionReporitory = tripTransactionReporitory;
    }

    @Override
    public List<Transaction> getTransactions(String tripId) {
        return List.of();
    }

    @Override
    public Transaction getTransaction(String tripId, String transactionId) {
        return null;
    }

    @Override
    public void deleteTransaction(String tripId, String transactionId) {

    }

    @Override
    public List<Transaction> createTransaction(String tripId, PostTransactionRequest transactionRequest) {
        return null;
    }
}
