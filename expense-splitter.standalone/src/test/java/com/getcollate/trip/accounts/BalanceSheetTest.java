package com.getcollate.trip.accounts;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.settler.Debt;
import com.getcollate.trip.accounts.settler.Settler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalanceSheetTest {

    @Test
    void shouldStartWithEmptyTransactions() {
        BalanceSheet balanceSheet = new BalanceSheet();

        assertNotNull(balanceSheet.getTransactions());
        assertTrue(balanceSheet.getTransactions().isEmpty());
    }

    @Test
    void addTransactionsShouldAppendTransactions() {
        BalanceSheet balanceSheet = new BalanceSheet();
        Participant payer = new Participant("Pranav", "p1");
        Transaction t1 = new Transaction(10f, payer, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(payer));
        Transaction t2 = new Transaction(20f, payer, CATEGORY.STAY, SHARETYPE.EQUAL, new Date(), List.of(payer));

        balanceSheet.addTransactions(List.of(t1, t2));

        assertEquals(2, balanceSheet.getTransactions().size());
        assertEquals(List.of(t1, t2), balanceSheet.getTransactions());
    }

    @Test
    void setTransactionsShouldReplaceExistingTransactions() {
        BalanceSheet balanceSheet = new BalanceSheet();
        Participant payer = new Participant("Pranav", "p1");
        Transaction existing = new Transaction(10f, payer, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(payer));
        Transaction replacement = new Transaction(99f, payer, CATEGORY.OTHERS, SHARETYPE.SPONSORED, new Date(), List.of(payer));

        balanceSheet.addTransactions(List.of(existing));
        balanceSheet.setTransactions(new ArrayList<>(List.of(replacement)));

        assertEquals(1, balanceSheet.getTransactions().size());
        assertEquals(replacement, balanceSheet.getTransactions().get(0));
    }

    @Test
    void settleShouldDelegateToSettlerWithSameTransactionList() {
        BalanceSheet balanceSheet = new BalanceSheet();
        Participant payer = new Participant("Pranav", "p1");
        Transaction t1 = new Transaction(10f, payer, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(payer));
        balanceSheet.addTransactions(List.of(t1));

        List<Debt> expected = List.of(new Debt("A", "B", 10f));
        CapturingSettler settler = new CapturingSettler(expected);

        List<Debt> actual = balanceSheet.settle(settler);

        assertSame(balanceSheet.getTransactions(), settler.receivedTransactions);
        assertEquals(expected, actual);
    }

    private static class CapturingSettler implements Settler {
        private final List<Debt> response;
        private List<Transaction> receivedTransactions;

        private CapturingSettler(List<Debt> response) {
            this.response = response;
        }

        @Override
        public List<Debt> settle(List<Transaction> transactions) {
            this.receivedTransactions = transactions;
            return response;
        }
    }
}
