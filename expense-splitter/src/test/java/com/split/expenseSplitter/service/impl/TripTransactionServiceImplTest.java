package com.split.expenseSplitter.service.impl;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.pojo.PostTransactionRequest;
import com.split.expenseSplitter.repository.TripTransactionReporitory;
import com.split.expenseSplitter.support.TestFixtures;
import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.CATEGORY;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripTransactionServiceImplTest {

    @Mock
    private TripTransactionReporitory tripTransactionReporitory;

    @InjectMocks
    private TripTransactionServiceImpl tripTransactionService;

    @Test
    void getTransactions_shouldDelegate() {
        Transaction tx = TestFixtures.transaction("tx-1", 10f, "P1", CATEGORY.FOOD, "01/01/2020", List.of("P1"));
        when(tripTransactionReporitory.getAllTransactions("trip-1")).thenReturn(List.of(tx));

        List<Transaction> result = tripTransactionService.getTransactions("trip-1");

        assertEquals(1, result.size());
        verify(tripTransactionReporitory).getAllTransactions("trip-1");
    }

    @Test
    void getTransaction_shouldDelegate() {
        Transaction tx = TestFixtures.transaction("tx-1", 10f, "P1", CATEGORY.FOOD, "01/01/2020", List.of("P1"));
        when(tripTransactionReporitory.getTransaction("trip-1", "tx-1")).thenReturn(tx);

        Transaction result = tripTransactionService.getTransaction("trip-1", "tx-1");

        assertEquals("tx-1", result.transactionId());
        verify(tripTransactionReporitory).getTransaction("trip-1", "tx-1");
    }

    @Test
    void deleteTransaction_shouldDelegate() {
        tripTransactionService.deleteTransaction("trip-1", "tx-1");
        verify(tripTransactionReporitory).deleteTransaction("trip-1", "tx-1");
    }

    @Test
    void createTransaction_shouldMapRequestIntoDomainTransactions() {
        Trip trip = new Trip("France", List.of(
                new Participant("Alice", "P1"),
                new Participant("Bob", "P2")
        ));
        trip.setTripId("trip-1");
        when(tripTransactionReporitory.getTripForTransaction("trip-1")).thenReturn(trip);

        PostTransactionRequest request = TestFixtures.request("P1", "FOOD", "01/01/2020", 3030, List.of("P1", "P2"));

        List<Transaction> result = tripTransactionService.createTransaction("trip-1", request);

        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(tripTransactionReporitory).createTransaction(org.mockito.ArgumentMatchers.eq("trip-1"), captor.capture());

        Transaction tx = captor.getValue().getFirst();
        assertEquals(3030f, tx.spentAmount());
        assertEquals("P1", tx.spentBy().participantId());
        assertEquals("FOOD", tx.spentOn().name());
        assertEquals(2, tx.benefittedBy().size());
        assertEquals(1, result.size());
    }

    @Test
    void createTransaction_shouldRejectInvalidCategory() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripTransactionReporitory.getTripForTransaction("trip-1")).thenReturn(trip);

        PostTransactionRequest request = TestFixtures.request("P1", "INVALID", "01/01/2020", 100, List.of("P1"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> tripTransactionService.createTransaction("trip-1", request));

        assertEquals("Invalid category: INVALID", ex.getMessage());
    }

    @Test
    void createTransaction_shouldRejectInvalidDate() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripTransactionReporitory.getTripForTransaction("trip-1")).thenReturn(trip);

        PostTransactionRequest request = TestFixtures.request("P1", "FOOD", "2020-01-01", 100, List.of("P1"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> tripTransactionService.createTransaction("trip-1", request));

        assertEquals("Invalid date format for: 2020-01-01", ex.getMessage());
    }

    @Test
    void createTransaction_shouldPropagateWhenSpentByParticipantIsMissing() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripTransactionReporitory.getTripForTransaction("trip-1")).thenReturn(trip);

        PostTransactionRequest request = TestFixtures.request("P9", "FOOD", "01/01/2020", 100, List.of("P1"));

        assertThrows(RuntimeException.class, () -> tripTransactionService.createTransaction("trip-1", request));
    }

    @Test
    void createTransaction_shouldPropagateWhenBeneficiaryParticipantIsMissing() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripTransactionReporitory.getTripForTransaction("trip-1")).thenReturn(trip);

        PostTransactionRequest request = TestFixtures.request("P1", "FOOD", "01/01/2020", 100, List.of("P9"));

        assertThrows(RuntimeException.class, () -> tripTransactionService.createTransaction("trip-1", request));
    }
}
