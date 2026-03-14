package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.expenseSplitter.pojo.PostTransactionRequest;
import com.getcollate.expenseSplitter.repository.TripTransactionReporitory;
import com.getcollate.expenseSplitter.service.TripTransactionService;
import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.CATEGORY;
import com.getcollate.trip.accounts.SHARETYPE;
import com.getcollate.trip.accounts.Transaction;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class TripTransactionServiceImpl implements TripTransactionService {

    TripTransactionReporitory tripTransactionReporitory;

    public TripTransactionServiceImpl(TripTransactionReporitory tripTransactionReporitory) {
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
                        new Participant(req.getSpentBy()), // Wrapping the String ID into a Participant object
                        CATEGORY.valueOf(req.getSpentOn().toUpperCase()), // Converting String to Enum
                        SHARETYPE.EQUAL, // NOTE: This was missing in your request DTO, defaulting to EQUAL
                        dateFormat.parse(req.getSpentDate()), // Parsing the DD/MM/YYYY string
                        req.getBenefittedBy().stream().map(Participant::new).toList() // Converting List<String> to List<Participant>
                );
            } catch (ParseException e) {
                throw new ValidationException("Invalid date format for: " + req.getSpentDate());
            }
        }).toList();
        tripTransactionReporitory.createTransaction(tripId, mappedTransactions);
        return mappedTransactions;
    }
}
