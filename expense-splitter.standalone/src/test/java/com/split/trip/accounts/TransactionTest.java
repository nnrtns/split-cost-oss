package com.split.trip.accounts;

import com.split.trip.Participant;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void convenienceConstructorShouldGenerateTransactionIdAndPopulateFields() {
        Participant payer = new Participant("Pranav", "p1");
        Date spentDate = new Date();

        Transaction transaction = new Transaction(
                100.13f,
                payer,
                CATEGORY.FOOD,
                SHARETYPE.EQUAL,
                spentDate,
                List.of(payer)
        );

        assertNotNull(transaction.transactionId());
        assertFalse(transaction.transactionId().isBlank());
        assertEquals(100.13f, transaction.spentAmount());
        assertEquals(payer, transaction.spentBy());
        assertEquals(CATEGORY.FOOD, transaction.spentOn());
        assertEquals(SHARETYPE.EQUAL, transaction.shareType());
        assertEquals(spentDate, transaction.spentDate());
        assertEquals(List.of(payer), transaction.benefittedBy());
    }

    @Test
    void canonicalConstructorShouldPreserveExplicitTransactionId() {
        Participant payer = new Participant("Pranav", "p1");
        Date spentDate = new Date();

        Transaction transaction = new Transaction(
                "txn-1",
                50.0f,
                payer,
                CATEGORY.TRANSPORT,
                SHARETYPE.SPONSORED,
                spentDate,
                List.of(payer)
        );

        assertEquals("txn-1", transaction.transactionId());
        assertEquals(50.0f, transaction.spentAmount());
        assertEquals(CATEGORY.TRANSPORT, transaction.spentOn());
        assertEquals(SHARETYPE.SPONSORED, transaction.shareType());
    }
}
