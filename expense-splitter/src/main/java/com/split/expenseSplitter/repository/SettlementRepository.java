package com.split.expenseSplitter.repository;

import com.split.trip.Trip;
import com.split.trip.accounts.settler.Debt;

import java.util.List;

public interface SettlementRepository {
    boolean persistsettlement(String tripId, List<Debt> settlements);
    Trip getTrip(String tripId);
}
