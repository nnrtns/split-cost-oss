package com.getcollate.expenseSplitter.controller;

import com.getcollate.expenseSplitter.pojo.PostTransactionRequest;
import com.getcollate.expenseSplitter.service.TripTransactionService;
import com.getcollate.trip.accounts.Transaction;
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

    @GetMapping
    public Map<String, Object> queryTransactions(@PathVariable String tripId) {
        return null;
    }

    TripTransactionService service;

    public TripTransactionController(TripTransactionService service) {
        this.service = service;
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Map<String, Object>> queryTransactions(@PathVariable String tripId, String transactionId) {
        logger.info("TripId: " + tripId + " TransactionId: " + transactionId);
        Transaction transaction = service.getTransaction(tripId, transactionId);
        return ResponseEntity.ok().body(Map.of("transactionId", transaction.transactionId(), "benefittedBy", transaction.benefittedBy(), "amount", transaction.spentAmount(), "shareType", transaction.shareType(), "spentBy", transaction.spentBy(), "spentDate", transaction.spentDate().toString(), "spentOn", transaction.spentOn()));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable String tripId, @PathVariable String transactionId) {
        logger.info("TripId: " + tripId + " TransactionId: " + transactionId);
        service.deleteTransaction(tripId, transactionId);
        return ResponseEntity.ok().body(Map.of("transactionId", transactionId));
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> postTransaction(@PathVariable String tripId, @Valid @RequestBody PostTransactionRequest transactionRequest) {
        logger.info("TripId: " + tripId + " Request: " + transactionRequest);
        List<Transaction> transactions = service.createTransaction(tripId, transactionRequest);
        return ResponseEntity.ok().body(transactions.stream()
                .map(transaction -> Map.of(
                        "transactionId", transaction.transactionId(),
                        "benefittedBy", transaction.benefittedBy(),
                        "amount", transaction.spentAmount(),
                        "shareType", transaction.shareType(),
                        "spentBy", transaction.spentBy(),
                        "spentDate", transaction.spentDate().toString(),
                        "spentOn", transaction.spentOn()
                )).toList());
    }
}
