package com.split.trip.accounts.settler.factory;

import com.split.trip.accounts.settler.BasicSettler;
import com.split.trip.accounts.settler.SettlementMode;
import com.split.trip.accounts.settler.Settler;
import com.split.trip.accounts.settler.SimplifiedSettler;

public final class SettlerFactory {
    private SettlerFactory() {}

    public static Settler create(SettlementMode mode) {
        return switch (mode) {
            case BASIC -> new BasicSettler();
            case SIMPLIFIED -> new SimplifiedSettler();
        };
    }
}
