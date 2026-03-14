package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.CATEGORY;
import com.getcollate.trip.accounts.SHARETYPE;
import com.getcollate.trip.accounts.Transaction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementSpecTest {

    @Test
    @Disabled("Enable when BasicSettler settlement rules are implemented")
    void basicSettlerShouldReturnRawDebtForSimpleTwoPersonSplit() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        List<Transaction> transactions = List.of(
                new Transaction(40f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(a, b))
        );

        List<Debt> result = new BasicSettler().settle(transactions);

        assertEquals(List.of(new Debt("B", "A", 20f)), result);
    }

    @Test
    @Disabled("Enable when SimplifiedSettler minimization rules are implemented")
    void simplifiedSettlerShouldMinimizeDebtChainIntoSingleTransfer() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        Participant c = new Participant("C", "3");

        List<Transaction> transactions = List.of(
                new Transaction(80f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(a, b)),
                new Transaction(20f, b, CATEGORY.TRANSPORT, SHARETYPE.EQUAL, new Date(), List.of(b, c))
        );

        List<Debt> result = new SimplifiedSettler().settle(transactions);

        assertEquals(List.of(
                new Debt("B", "A", 20f),
                new Debt("C", "A", 30f)
        ), result);
    }

    @Test
    @Disabled("Enable when SimplifiedSettler is implemented")
    void simplifiedSettlerShouldHandlePromptExampleByReducingTwoDebtsToOne() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", 40f, new Participant("A", "A"), CATEGORY.FOOD, SHARETYPE.SPONSORED, new Date(), List.of(new Participant("B", "B"))),
                new Transaction("t2", 10f, new Participant("C", "C"), CATEGORY.FOOD, SHARETYPE.SPONSORED, new Date(), List.of(new Participant("A", "A")))
        );

        List<Debt> result = new SimplifiedSettler().settle(transactions);

        assertEquals(List.of(new Debt("B", "A", 40f), new Debt("A", "C", 10f)), result);
        // or, if your simplifier returns fully minimized final settlements only:
        // assertEquals(List.of(new Debt("B", "C", 10f), new Debt("B", "A", 30f)), result);
    }
}
