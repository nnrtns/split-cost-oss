package com.split.trip.accounts.settler;

import com.split.trip.Participant;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.SHARETYPE;

import java.util.*;

public class SimplifiedSettler implements Settler {

    @Override
    public List<Debt> settle(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> netBalancesInCents = buildNetBalancesInCents(transactions);

        List<String> participantIds = new ArrayList<>();
        List<Integer> balances = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : netBalancesInCents.entrySet()) {
            if (entry.getValue() != 0) {
                participantIds.add(entry.getKey());
                balances.add(entry.getValue());
            }
        }

        if (balances.isEmpty()) {
            return List.of();
        }

        int n = balances.size();
        if (n > 20) {
            throw new IllegalStateException("Exact simplified settlement supports up to 20 non-zero participants.");
        }

        int totalMasks = 1 << n;
        int[] subsetSum = new int[totalMasks];

        for (int mask = 1; mask < totalMasks; mask++) {
            int lsb = mask & -mask;
            int bitIndex = Integer.numberOfTrailingZeros(lsb);
            subsetSum[mask] = subsetSum[mask ^ lsb] + balances.get(bitIndex);
        }

        int[] memo = new int[totalMasks];
        int[] choice = new int[totalMasks];
        Arrays.fill(memo, Integer.MIN_VALUE);
        memo[0] = 0;

        maxZeroSumGroups(totalMasks - 1, subsetSum, memo, choice);

        List<Debt> result = new ArrayList<>();
        int remainingMask = totalMasks - 1;

        while (remainingMask != 0) {
            int groupMask = choice[remainingMask];
            result.addAll(settleZeroSumGroup(groupMask, participantIds, balances));
            remainingMask ^= groupMask;
        }

        return result;
    }

    /**
     * Exact DP:
     * Maximize number of disjoint zero-sum groups in "mask".
     * Minimum transactions = n - (#zero-sum groups).
     */
    private int maxZeroSumGroups(
            int mask,
            int[] subsetSum,
            int[] memo,
            int[] choice
    ) {
        if (memo[mask] != Integer.MIN_VALUE) {
            return memo[mask];
        }

        int firstBit = mask & -mask;

        int best = Integer.MIN_VALUE;
        int bestSubmask = 0;

        for (int submask = mask; submask > 0; submask = (submask - 1) & mask) {
            if ((submask & firstBit) == 0) {
                continue;
            }

            if (subsetSum[submask] != 0) {
                continue;
            }

            int candidate = 1 + maxZeroSumGroups(mask ^ submask, subsetSum, memo, choice);

            if (candidate > best) {
                best = candidate;
                bestSubmask = submask;
            } else if (candidate == best) {
                // tie-break: prefer smaller groups for cleaner reconstruction
                if (bestSubmask == 0 || Integer.bitCount(submask) < Integer.bitCount(bestSubmask)) {
                    bestSubmask = submask;
                }
            }
        }

        memo[mask] = best;
        choice[mask] = bestSubmask;
        return best;
    }

    /**
     * Settle one zero-sum group with actual transfers.
     * For a zero-sum group of size m, this will use at most m-1 transfers.
     * Since the DP maximizes number of disjoint zero-sum groups, the overall
     * result is globally optimal in number of transfers.
     */
    private List<Debt> settleZeroSumGroup(
            int groupMask,
            List<String> participantIds,
            List<Integer> balances
    ) {
        Deque<BalanceNode> debtors = new ArrayDeque<>();
        Deque<BalanceNode> creditors = new ArrayDeque<>();

        for (int i = 0; i < balances.size(); i++) {
            if ((groupMask & (1 << i)) == 0) {
                continue;
            }

            int balance = balances.get(i);
            if (balance < 0) {
                debtors.addLast(new BalanceNode(participantIds.get(i), -balance));
            } else if (balance > 0) {
                creditors.addLast(new BalanceNode(participantIds.get(i), balance));
            }
        }

        List<Debt> debts = new ArrayList<>();

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            BalanceNode debtor = debtors.peekFirst();
            BalanceNode creditor = creditors.peekFirst();

            int transferInCents = Math.min(debtor.amountInCents, creditor.amountInCents);

            debts.add(createDebt(debtor.participantId, creditor.participantId, transferInCents));

            debtor.amountInCents -= transferInCents;
            creditor.amountInCents -= transferInCents;

            if (debtor.amountInCents == 0) {
                debtors.removeFirst();
            }
            if (creditor.amountInCents == 0) {
                creditors.removeFirst();
            }
        }

        return debts;
    }

    private Debt createDebt(String fromParticipant, String toParticipant, int amountInCents) {
        return new Debt(fromParticipant, toParticipant, fromCents(amountInCents));
    }

    private Map<String, Integer> buildNetBalancesInCents(List<Transaction> transactions) {
        Map<String, Integer> netBalancesInCents = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            validate(transaction);

            String payerId = participantKey(transaction.spentBy());
            ensurePresent(netBalancesInCents, payerId);

            for (Participant participant : transaction.benefittedBy()) {
                ensurePresent(netBalancesInCents, participantKey(participant));
            }

            if (transaction.shareType() == SHARETYPE.SPONSORED) {
                continue;
            }

            int totalAmountInCents = toCents(transaction.spentAmount());

            netBalancesInCents.put(
                    payerId,
                    netBalancesInCents.get(payerId) + totalAmountInCents
            );

            Map<String, Integer> sharesInCents =
                    splitEquallyInCents(totalAmountInCents, transaction.benefittedBy());

            for (Map.Entry<String, Integer> entry : sharesInCents.entrySet()) {
                String participantId = entry.getKey();
                int shareInCents = entry.getValue();

                netBalancesInCents.put(
                        participantId,
                        netBalancesInCents.get(participantId) - shareInCents
                );
            }
        }

        return netBalancesInCents;
    }

    private Map<String, Integer> splitEquallyInCents(int totalAmountInCents, List<Participant> beneficiaries) {
        int count = beneficiaries.size();
        int baseShare = totalAmountInCents / count;
        int remainder = totalAmountInCents % count;

        Map<String, Integer> shares = new LinkedHashMap<>();

        for (int i = 0; i < beneficiaries.size(); i++) {
            Participant participant = beneficiaries.get(i);
            int share = baseShare + (i < remainder ? 1 : 0);
            shares.put(participantKey(participant), share);
        }

        return shares;
    }

    private void ensurePresent(Map<String, Integer> netBalancesInCents, String participantId) {
        netBalancesInCents.putIfAbsent(participantId, 0);
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
    }

    private int toCents(Float amount) {
        return Math.round(amount * 100.0f);
    }

    private float fromCents(int cents) {
        return cents / 100.0f;
    }

    private String participantKey(Participant participant) {
        return participant.participantId();
    }

    private static class BalanceNode {
        private final String participantId;
        private int amountInCents;

        private BalanceNode(String participantId, int amountInCents) {
            this.participantId = participantId;
            this.amountInCents = amountInCents;
        }
    }
}
