package com.getcollate.expenseSplitter.service;

import com.getcollate.expenseSplitter.exception.DuplicateInsertionException;
import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.expenseSplitter.repository.SettlementRepository;
import com.getcollate.trip.accounts.settler.Debt;

import java.util.List;

public interface SettlementService {

    List<Debt> settle(String tripId, boolean settlementMode)  throws ValidationException;

}
