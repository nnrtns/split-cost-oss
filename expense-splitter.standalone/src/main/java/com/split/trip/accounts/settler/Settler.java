package com.split.trip.accounts.settler;

import com.split.trip.accounts.Transaction;

import java.util.List;

public interface Settler {
    public List<Debt> settle(List<Transaction> transactions);
}
