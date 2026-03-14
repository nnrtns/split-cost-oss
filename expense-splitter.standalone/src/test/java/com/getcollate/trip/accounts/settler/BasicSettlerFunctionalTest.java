package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.CATEGORY;
import com.getcollate.trip.accounts.SHARETYPE;
import com.getcollate.trip.accounts.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.getcollate.trip.accounts.settler.SettlementTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class BasicSettlerFunctionalTest {

    private final BasicSettler settler = new BasicSettler();

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
    void settleShouldRejectNullBeneficiary() {
        Participant a = new Participant("A", "1");
        List<Participant> beneficiaries = new ArrayList<>();
        beneficiaries.add(a);
        beneficiaries.add(null);
        Transaction t = new Transaction(10f, a, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), beneficiaries);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> settler.settle(List.of(t)));
        assertEquals("Beneficiary cannot be null", ex.getMessage());
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
    void settleShouldMergeMultipleDebtsBetweenSameDebtorAndCreditor() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");

        Transaction first = equalExpense(40_00, a, List.of(a, b));
        Transaction second = equalExpense(20_00, a, List.of(a, b));

        List<Debt> result = settler.settle(List.of(first, second));

        assertDebtsExactlyMatch("Merged raw debt should add cents across matching pairs", List.of(new Debt("B", "A", 30.00f)).stream().collect(java.util.stream.Collectors.toMap(d -> edge(d.from(), d.to()), d -> toCents(d.amount()), (x, y) -> y, java.util.LinkedHashMap::new)), result);
    }

    @Test
    void settleShouldRespectBeneficiaryOrderWhenDistributingRemainder() {
        Participant a = new Participant("A", "1");
        Participant b = new Participant("B", "2");
        Participant c = new Participant("C", "3");

        Transaction transaction = equalExpense(1_00, a, List.of(b, c, a));

        assertDebtsExactlyMatch(
                "First beneficiary should receive the first extra cent from remainder distribution",
                java.util.Map.of(edge("B", "A"), 34, edge("C", "A"), 33),
                settler.settle(List.of(transaction))
        );
    }

    @TestFactory
    Stream<org.junit.jupiter.api.DynamicTest> singleExpenseShouldProduceExpectedRawDebtsForEveryParticipantCountFrom2To20() {
        return IntStream.rangeClosed(2, 20).mapToObj(count -> org.junit.jupiter.api.DynamicTest.dynamicTest(
                "basic single expense exact raw debts for participant count " + count,
                () -> {
                    List<Participant> participants = participants(count);
                    Participant payer = participants.getFirst();
                    int totalCents = (count * 113) + (count / 2);
                    Transaction transaction = equalExpense(totalCents, payer, participants);

                    List<Debt> result = settler.settle(List.of(transaction));
                    assertDebtsExactlyMatch(
                            "Single expense raw debt map should match for participant count " + count,
                            expectedSingleExpenseDebtMapInCents(totalCents, participants, payer),
                            result
                    );
                }
        ));
    }
}
