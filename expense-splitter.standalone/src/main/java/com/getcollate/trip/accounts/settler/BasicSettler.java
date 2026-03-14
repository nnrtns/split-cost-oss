package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.Transaction;
import com.getcollate.trip.accounts.SHARETYPE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class BasicSettler implements Settler {
    @Override
    public List<Debt> settle(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return List.of();
        }

        Map<DebtKey, Integer> mergedDebtsInCents = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            validate(transaction);

            if (transaction.shareType() == SHARETYPE.SPONSORED) {
                continue;
            }

            int totalAmountInCents = toCents(transaction.spentAmount());
            Map<String, Integer> participantShareInCents =
                    splitEquallyInCents(totalAmountInCents, transaction.benefittedBy());

            String payerName = participantKey(transaction.spentBy());

            for (Participant beneficiary : transaction.benefittedBy()) {
                String beneficiaryName = participantKey(beneficiary);

                if (beneficiaryName.equals(payerName)) {
                    continue;
                }

                int shareInCents = participantShareInCents.get(beneficiaryName);
                if (shareInCents <= 0) {
                    continue;
                }

                DebtKey debtKey = new DebtKey(beneficiaryName, payerName);
                mergedDebtsInCents.merge(debtKey, shareInCents, Integer::sum);
            }
        }

        return toDebtList(mergedDebtsInCents);
    }

    private void validate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.spentAmount() == null || transaction.spentAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (transaction.spentBy() == null) {
            throw new IllegalArgumentException("Transaction payer cannot be null");
        }
        if (transaction.shareType() == null) {
            throw new IllegalArgumentException("Transaction share type cannot be null");
        }
        if (transaction.benefittedBy() == null || transaction.benefittedBy().isEmpty()) {
            throw new IllegalArgumentException("At least one beneficiary is required");
        }

        for (Participant participant : transaction.benefittedBy()) {
            if (participant == null) {
                throw new IllegalArgumentException("Beneficiary cannot be null");
            }
        }
    }

    private Map<String, Integer> splitEquallyInCents(int totalAmountInCents, List<Participant> beneficiaries) {
        int totalBeneficiaries = beneficiaries.size();
        int baseShare = totalAmountInCents / totalBeneficiaries;
        int remainder = totalAmountInCents % totalBeneficiaries;

        Map<String, Integer> shares = new LinkedHashMap<>();

        for (int i = 0; i < beneficiaries.size(); i++) {
            Participant participant = beneficiaries.get(i);
            int share = baseShare + (i < remainder ? 1 : 0);
            shares.put(participantKey(participant), share);
        }

        return shares;
    }

    private List<Debt> toDebtList(Map<DebtKey, Integer> mergedDebtsInCents) {
        List<Debt> result = new ArrayList<>();

        for (Map.Entry<DebtKey, Integer> entry : mergedDebtsInCents.entrySet()) {
            DebtKey key = entry.getKey();
            result.add(new Debt(key.debtor(), key.creditor(), fromCents(entry.getValue())));
        }

        return result;
    }

    private int toCents(Float amount) {
        return Math.round(amount * 100.0f);
    }

    private Float fromCents(int cents) {
        return cents / 100.0f;
    }

    private String participantKey(Participant participant) {
        return participant.name();
    }

    private record DebtKey(String debtor, String creditor) {}
}
