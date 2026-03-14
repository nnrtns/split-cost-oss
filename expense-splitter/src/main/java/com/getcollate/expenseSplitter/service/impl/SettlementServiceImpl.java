package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.repository.SettlementRepository;
import com.getcollate.expenseSplitter.service.SettlementService;
import com.getcollate.trip.Trip;
import com.getcollate.trip.accounts.settler.Debt;
import com.getcollate.trip.accounts.settler.SettlementMode;
import com.getcollate.trip.accounts.settler.factory.SettlerFactory;
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
        Trip trip = settlementRepository.getTrip(tripId);
        List<Debt> settlement = trip.settle(SettlerFactory.create(settlementMode?SettlementMode.SIMPLIFIED:SettlementMode.BASIC));
        settlementRepository.persistsettlement(tripId, settlement);
        return settlement;
    }
}
