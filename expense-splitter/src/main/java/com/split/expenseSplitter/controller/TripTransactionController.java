package com.split.expenseSplitter.controller;

import com.split.expenseSplitter.pojo.PostTransactionRequest;
import com.split.expenseSplitter.service.TripTransactionService;
import com.split.trip.accounts.Transaction;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trip/{tripId}/transactions")
public class TripTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TripTransactionController.class);

    private final TripTransactionService service;

    public TripTransactionController(TripTransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions(@PathVariable String tripId) {
        logger.info("TripId: {}", tripId);

        List<Transaction> transactions = service.getTransactions(tripId);

        return ResponseEntity.ok(
                transactions.stream()
                        .map(transaction -> Map.of(
                                "transactionId", transaction.transactionId(),
                                "benefittedBy", transaction.benefittedBy(),
                                "amount", transaction.spentAmount(),
                                "shareType", transaction.shareType(),
                                "spentBy", transaction.spentBy(),
                                "spentDate", transaction.spentDate().toString(),
                                "spentOn", transaction.spentOn()
                        ))
                        .toList()
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransaction(
            @PathVariable String tripId,
            @PathVariable String transactionId
    ) {
        logger.info("TripId: {} TransactionId: {}", tripId, transactionId);

        Transaction transaction = service.getTransaction(tripId, transactionId);

        return ResponseEntity.ok(
                Map.of(
                        "transactionId", transaction.transactionId(),
                        "benefittedBy", transaction.benefittedBy(),
                        "amount", transaction.spentAmount(),
                        "shareType", transaction.shareType(),
                        "spentBy", transaction.spentBy(),
                        "spentDate", transaction.spentDate().toString(),
                        "spentOn", transaction.spentOn()
                )
        );
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable String tripId,
            @PathVariable String transactionId
    ) {
        logger.info("TripId: {} TransactionId: {}", tripId, transactionId);
        service.deleteTransaction(tripId, transactionId);
        return ResponseEntity.ok(Map.of("transactionId", transactionId));
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> postTransaction(
            @PathVariable String tripId,
            @Valid @RequestBody PostTransactionRequest transactionRequest
    ) {
        logger.info("TripId: {} Request: {}", tripId, transactionRequest);

        List<Transaction> transactions = service.createTransaction(tripId, transactionRequest);

        return ResponseEntity.ok(
                transactions.stream()
                        .map(transaction -> Map.of(
                                "transactionId", transaction.transactionId(),
                                "benefittedBy", transaction.benefittedBy(),
                                "amount", transaction.spentAmount(),
                                "shareType", transaction.shareType(),
                                "spentBy", transaction.spentBy(),
                                "spentDate", transaction.spentDate().toString(),
                                "spentOn", transaction.spentOn()
                        ))
                        .toList()
        );
    }
}