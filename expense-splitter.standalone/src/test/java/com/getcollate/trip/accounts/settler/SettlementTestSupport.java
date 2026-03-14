package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.CATEGORY;
import com.getcollate.trip.accounts.SHARETYPE;
import com.getcollate.trip.accounts.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SettlementTestSupport {
    static final Date FIXED_DATE = new Date(1577836800000L);

    private SettlementTestSupport() {
    }

    static List<Participant> participants(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new Participant("P" + i, "id-" + i))
                .collect(Collectors.toList());
    }

    static float amountFromCents(int cents) {
        return BigDecimal.valueOf(cents).movePointLeft(2).floatValue();
    }

    static Transaction equalExpense(int totalCents, Participant payer, List<Participant> beneficiaries) {
        return new Transaction(
                amountFromCents(totalCents),
                payer,
                CATEGORY.FOOD,
                SHARETYPE.EQUAL,
                FIXED_DATE,
                beneficiaries
        );
    }

    static Transaction sponsoredExpense(int totalCents, Participant payer, List<Participant> beneficiaries) {
        return new Transaction(
                amountFromCents(totalCents),
                payer,
                CATEGORY.OTHERS,
                SHARETYPE.SPONSORED,
                FIXED_DATE,
                beneficiaries
        );
    }

    static int toCents(float amount) {
        return Math.round(amount * 100.0f);
    }

    static String edge(String from, String to) {
        return from + "->" + to;
    }

    static Map<String, Integer> debtMapInCents(List<Debt> debts) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Debt debt : debts) {
            map.merge(edge(debt.from(), debt.to()), toCents(debt.amount()), Integer::sum);
        }
        return map;
    }

    static Map<String, Integer> expectedSingleExpenseDebtMapInCents(int totalCents, List<Participant> beneficiaries, Participant payer) {
        int count = beneficiaries.size();
        int baseShare = totalCents / count;
        int remainder = totalCents % count;

        Map<String, Integer> expected = new LinkedHashMap<>();
        for (int i = 0; i < beneficiaries.size(); i++) {
            Participant beneficiary = beneficiaries.get(i);
            int share = baseShare + (i < remainder ? 1 : 0);
            if (!beneficiary.name().equals(payer.name()) && share > 0) {
                expected.put(edge(beneficiary.name(), payer.name()), share);
            }
        }
        return expected;
    }

    static Map<String, Integer> computeNetBalancesInCents(List<Transaction> transactions) {
        Map<String, Integer> net = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            String payer = transaction.spentBy().name();
            net.putIfAbsent(payer, 0);
            for (Participant participant : transaction.benefittedBy()) {
                net.putIfAbsent(participant.name(), 0);
            }

            if (transaction.shareType() == SHARETYPE.SPONSORED) {
                continue;
            }

            int totalCents = toCents(transaction.spentAmount());
            net.put(payer, net.get(payer) + totalCents);

            int count = transaction.benefittedBy().size();
            int baseShare = totalCents / count;
            int remainder = totalCents % count;
            for (int i = 0; i < count; i++) {
                Participant beneficiary = transaction.benefittedBy().get(i);
                int share = baseShare + (i < remainder ? 1 : 0);
                net.put(beneficiary.name(), net.get(beneficiary.name()) - share);
            }
        }

        return net;
    }

    static void assertDebtsExactlyMatch(String message, Map<String, Integer> expected, List<Debt> actual) {
        assertEquals(expected, debtMapInCents(actual), message);
    }

    static void assertDebtsSettleBalances(List<Transaction> transactions, List<Debt> debts) {
        Map<String, Integer> balances = new LinkedHashMap<>(computeNetBalancesInCents(transactions));

        for (Debt debt : debts) {
            int amount = toCents(debt.amount());
            balances.putIfAbsent(debt.from(), 0);
            balances.putIfAbsent(debt.to(), 0);
            balances.put(debt.from(), balances.get(debt.from()) + amount);
            balances.put(debt.to(), balances.get(debt.to()) - amount);
        }

        for (Map.Entry<String, Integer> entry : balances.entrySet()) {
            assertEquals(0, entry.getValue().intValue(), "Balance not settled for participant " + entry.getKey());
        }
    }

    static int nonZeroBalanceCount(List<Transaction> transactions) {
        int count = 0;
        for (int value : computeNetBalancesInCents(transactions).values()) {
            if (value != 0) {
                count++;
            }
        }
        return count;
    }
}
