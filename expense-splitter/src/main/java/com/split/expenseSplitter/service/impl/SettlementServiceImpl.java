package com.split.expenseSplitter.service.impl;

import com.split.expenseSplitter.repository.SettlementRepository;
import com.split.expenseSplitter.service.SettlementService;
import com.split.trip.Trip;
import com.split.trip.accounts.settler.Debt;
import com.split.trip.accounts.settler.SettlementMode;
import com.split.trip.accounts.settler.factory.SettlerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementServiceImpl implements SettlementService {

    SettlementRepository settlementRepository;

    public SettlementServiceImpl(@Qualifier("postgresSettlementRepository") SettlementRepository settlementRepository) {
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
