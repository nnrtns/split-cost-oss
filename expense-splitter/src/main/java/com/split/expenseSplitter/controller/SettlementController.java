package com.split.expenseSplitter.controller;

import com.split.expenseSplitter.service.SettlementService;
import com.split.trip.accounts.settler.Debt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trip/{tripId}/settlement")
public class SettlementController {

    private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);

    private final SettlementService service;

    public SettlementController(SettlementService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<Map<String, String>>> settle(@PathVariable String tripId, @RequestParam boolean simplify) {
        logger.info("TripId: " + tripId + " Simplify: " + simplify);
        List<Debt> debts = service.settle(tripId, simplify);
        return ResponseEntity.ok().body(
                debts.stream().map((Debt debt) -> Map.of("from", debt.from(), "to", debt.to(), "amount", String.valueOf(debt.amount()))).toList()
        );
    }
}
