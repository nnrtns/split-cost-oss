package com.getcollate.expenseSplitter.repository;

import com.getcollate.trip.accounts.settler.Debt;

import java.util.List;

public interface SettlementRepository {

    boolean persistsettlement(String tripId, List<Debt> settlements);
}
