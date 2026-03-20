package com.split.expenseSplitter.support;

import com.split.expenseSplitter.pojo.PostTransactionRequest;
import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.settler.Debt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static Trip trip(String name, String... participantIds) {
        List<Participant> participants = java.util.Arrays.stream(participantIds)
                .map(id -> new Participant(id, id))
                .toList();
        return new Trip(name, participants);
    }

    public static Participant participant(String id) {
        return new Participant(id, id);
    }

    public static Date date(String value) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Transaction transaction(
            String transactionId,
            float amount,
            String spentBy,
            CATEGORY category,
            String spentDate,
            List<String> beneficiaries
    ) {
        return new Transaction(
                transactionId,
                amount,
                participant(spentBy),
                category,
                SHARETYPE.EQUAL,
                date(spentDate),
                beneficiaries.stream().map(TestFixtures::participant).toList()
        );
    }

    public static Debt debt(String from, String to, float amount) {
        return new Debt(from, to, amount);
    }

    public static PostTransactionRequest request(String spentBy, String spentOn, String spentDate, int amount, List<String> beneficiaries) {
        PostTransactionRequest request = new PostTransactionRequest();
        PostTransactionRequest.RequestTransactions row = new PostTransactionRequest.RequestTransactions();
        row.setSpentBy(spentBy);
        row.setSpentOn(spentOn);
        row.setSpentDate(spentDate);
        row.setSpentAmount(amount);
        row.setBenefittedBy(beneficiaries);
        request.setTransactions(List.of(row));
        return request;
    }
}
