package com.split.trip.accounts.settler.factory;

import com.split.trip.accounts.settler.BasicSettler;
import com.split.trip.accounts.settler.SettlementMode;
import com.split.trip.accounts.settler.Settler;
import com.split.trip.accounts.settler.SimplifiedSettler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SettlerFactoryTest {

    @Test
    void shouldCreateBasicSettler() {
        Settler settler = SettlerFactory.create(SettlementMode.BASIC);

        assertInstanceOf(BasicSettler.class, settler);
    }

    @Test
    void shouldCreateSimplifiedSettler() {
        Settler settler = SettlerFactory.create(SettlementMode.SIMPLIFIED);

        assertInstanceOf(SimplifiedSettler.class, settler);
    }
}
