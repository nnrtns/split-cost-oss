package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.repository.SettlementRepository;
import com.getcollate.expenseSplitter.service.SettlementService;
import com.getcollate.trip.accounts.settler.Debt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementServiceImpl implements SettlementService {

    SettlementRepository settlementRepository;

    public SettlementServiceImpl(SettlementRepository settlementRepository) {
        this.settlementRepository = settlementRepository;
    }

    @Override
    public List<Debt> settle(String tripId, boolean settlementMode) {
        return List.of();
    }
}
