package com.split.expenseSplitter.service;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.trip.accounts.settler.Debt;

import java.util.List;

public interface SettlementService {

    List<Debt> settle(String tripId, boolean settlementMode)  throws ValidationException;

}
