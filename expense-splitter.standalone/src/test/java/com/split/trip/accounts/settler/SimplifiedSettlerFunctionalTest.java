package com.split.trip.accounts.settler;

import com.split.trip.Participant;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.split.trip.accounts.settler.SettlementTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class SimplifiedSettlerFunctionalTest {

    private final SimplifiedSettler settler = new SimplifiedSettler();

    @Test
    void settleShouldReturnEmptyListForNullTransactions() {
        assertTrue(settler.settle(null).isEmpty());
    }

    @Test
    void settleShouldReturnEmptyListForEmptyTransactions() {
        assertTrue(settler.settle(List.of()).isEmpty());
    }

    @Test
    void settleShouldRejectNullTransaction() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> settler.settle(java.util.Collections.singletonList((Transaction) null))
        );
        assertEquals("Transaction cannot be null", ex.getMessage());
    }

    @Test
    void settleShouldRejectZeroAmount() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(0f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(a));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("Transaction amount must be positive", ex.getMessage());
    }

    @Test
    void settleShouldRejectNegativeAmount() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(-1f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(a));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("Transaction amount must be positive", ex.getMessage());
    }

    @Test
    void settleShouldRejectNullPayer() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(10f, null, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of(a));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("Transaction payer cannot be null", ex.getMessage());
    }

    @Test
    void settleShouldRejectNullShareType() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(10f, a, CATEGORY.FOOD, null, new Date(), List.of(a));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("Transaction share type cannot be null", ex.getMessage());
    }

    @Test
    void settleShouldRejectNullBeneficiaries() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(10f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("At least one beneficiary is required", ex.getMessage());
    }

    @Test
    void settleShouldRejectEmptyBeneficiaries() {
        Participant a = new Participant("A", "1");
        Transaction t = new Transaction(10f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("At least one beneficiary is required", ex.getMessage());
    }

    @Test
    void settleShouldIgnoreSponsoredTransactions() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        Transaction sponsored = sponsoredExpense(10_00, a, List.of(a, b));

        assertTrue(settler.settle(List.of(sponsored)).isEmpty());
    }

    @Test
    void settleShouldReturnNoDebtWhenPayerIsOnlyBeneficiary() {
        Participant a = new Participant("A", "1");
        Transaction transaction = equalExpense(10_00, a, List.of(a));

        assertTrue(settler.settle(List.of(transaction)).isEmpty());
    }

    @Test
    void settleShouldCollapseNeutralMiddleParticipantIntoSingleDebt() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        Participant c = new Participant("C", "3");

        Transaction t1 = equalExpense(100_00, a, List.of(a, b));
        Transaction t2 = equalExpense(100_00, b, List.of(b, c));

        List<Debt> result = settler.settle(List.of(t1, t2));

        assertDebtsExactlyMatch(
                "Neutral middle participant should disappear after simplification",
                Map.of(edge("3", "1"), 50_00),
                result
        );
    }

    @Test
    void settleShouldReduceThreeParticipantChainIntoMinimalFinalDebts() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        Participant c = new Participant("C", "3");

        Transaction t1 = equalExpense(90_00, a, List.of(a, b, c));
        Transaction t2 = equalExpense(30_00, b, List.of(b, c));

        List<Debt> result = settler.settle(List.of(t1, t2));

        assertDebtsExactlyMatch(
                "Chain should simplify to the final net balances only",
                Map.of(edge("3", "1"), 45_00, edge("2", "1"), 15_00),
                result
        );
        assertDebtsSettleBalances(List.of(t1, t2), result);
        assertEquals(2, result.size());
    }

    @Test
    void settleShouldHandlePromptStyleFranceTripExampleWithCentRemainders() {
        Participant alice = new Participant("Alice", "Alice");
        Participant bob = new Participant("Bob", "Bob");
        Participant smith = new Participant("Smith", "Smith");
        Participant wonder = new Participant("Wonder", "Wonder");
        Participant mat = new Participant("Mat", "Mat");
        Participant philips = new Participant("Philips", "Philips");
        Participant aleena = new Participant("Aleena", "Aleena");
        Participant kimberly = new Participant("Kimberly", "Kimberly");

        List<Participant> seven = List.of(bob, smith, mat, alice, philips, aleena, kimberly);
        List<Transaction> transactions = List.of(
                new Transaction(100.0f, alice, CATEGORY.FOOD, SHARETYPE.EQUAL, FIXED_DATE, seven),
                new Transaction(100.0f, bob, CATEGORY.TRANSPORT, SHARETYPE.EQUAL, FIXED_DATE, seven),
                new Transaction(100.0f, smith, CATEGORY.FOOD, SHARETYPE.EQUAL, FIXED_DATE, seven),
                new Transaction(100.0f, alice, CATEGORY.STAY, SHARETYPE.EQUAL, FIXED_DATE, seven)
        );

        List<Debt> result = settler.settle(transactions);

        assertDebtsSettleBalances(transactions, result);
        assertEquals(6, result.size());
        assertFalse(result.stream().anyMatch(d -> d.from().equals(wonder.participantId()) || d.to().equals(wonder.participantId())));
    }

    @TestFactory
    Stream<org.junit.jupiter.api.DynamicTest> singleExpenseShouldProduceExpectedMinimalSettlementForEveryParticipantCountFrom2To20() {
        return IntStream.rangeClosed(2, 20).mapToObj(count -> org.junit.jupiter.api.DynamicTest.dynamicTest(
                "simplified single expense exact settlement for participant count " + count,
                () -> {
                    List<Participant> participants = participants(count);
                    Participant payer = participants.getFirst();
                    int totalCents = (count * 127) + (count / 3);
                    Transaction transaction = equalExpense(totalCents, payer, participants);

                    List<Debt> result = settler.settle(List.of(transaction));
                    assertDebtsExactlyMatch(
                            "Single expense simplified settlement should match expected final debts for participant count " + count,
                            expectedSingleExpenseDebtMapInCents(totalCents, participants, payer),
                            result
                    );
                    assertDebtsSettleBalances(List.of(transaction), result);
                    assertEquals(count - 1, result.size());
                }
        ));
    }

    @TestFactory
    Stream<org.junit.jupiter.api.DynamicTest> twoCreditorScenarioShouldAlwaysSettleBalancesWithinNonZeroBoundFrom2To20() {
        return IntStream.rangeClosed(2, 20).mapToObj(count -> org.junit.jupiter.api.DynamicTest.dynamicTest(
                "simplified two-creditor invariant for participant count " + count,
                () -> {
                    List<Participant> participants = participants(count);
                    Participant p1 = participants.get(0);
                    Participant p2 = participants.get(1);

                    List<Transaction> transactions;
                    if (count == 2) {
                        transactions = List.of(
                                equalExpense(75_00, p1, participants),
                                equalExpense(25_00, p2, participants)
                        );
                    } else {
                        List<Participant> everyone = participants;
                        List<Participant> tail = participants.subList(1, participants.size());
                        transactions = List.of(
                                equalExpense(120_00 + count, p1, everyone),
                                equalExpense(60_00 + count, p2, tail)
                        );
                    }

                    List<Debt> result = settler.settle(transactions);
                    assertDebtsSettleBalances(transactions, result);
                    assertTrue(result.size() <= Math.max(0, nonZeroBalanceCount(transactions) - 1));
                }
        ));
    }
}
