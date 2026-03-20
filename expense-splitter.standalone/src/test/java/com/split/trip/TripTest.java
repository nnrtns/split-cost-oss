package com.split.trip;

import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.settler.Debt;
import com.split.trip.accounts.settler.Settler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.split.trip.accounts.CATEGORY.FOOD;
import static com.split.trip.accounts.SHARETYPE.EQUAL;
import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    @Test
    void constructorShouldRejectNullParticipants() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> new Trip("Bangkok", null));

        assertEquals("Participants cannot be empty", ex.getMessage());
    }

    @Test
    void constructorShouldRejectEmptyParticipants() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> new Trip("Bangkok", List.of()));

        assertEquals("Participants cannot be empty", ex.getMessage());
    }

    @Test
    void constructorShouldCreateTripForValidParticipants() {
        assertDoesNotThrow(() -> new Trip(
                "Bangkok",
                List.of(new Participant("A", "1"), new Participant("B", "2"))
        ));
    }

    @Test
    void getParticipantShouldReturnParticipantById() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1"), new Participant("B", "2")));

        Participant participant = trip.getParticipant("1");

        assertEquals("A", participant.name());
        assertEquals("1", participant.participantId());
    }

    @Test
    void getParticipantShouldThrowWhenIdDoesNotExist() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trip.getParticipant("99"));

        assertTrue(ex.getMessage().contains("Participant not found in the trip"));
    }

    @Test
    void addParticipantsShouldMakeNewParticipantRetrievableById() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));

        trip.addParticipants(List.of(new Participant("B", "2")));

        assertEquals("B", trip.getParticipant("2").name());
    }

    @Test
    void removeParticipantsShouldRemoveParticipantFromLookupById() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1"), new Participant("B", "2")));

        trip.removeParticipants(List.of("2"));

        assertThrows(RuntimeException.class, () -> trip.getParticipant("2"));
    }

    @Test
    void addTransactionsShouldForwardTransactionsIntoBalanceSheet() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));
        Transaction transaction = new Transaction(10f, new Participant("A", "1"), FOOD, EQUAL, new java.util.Date(), List.of(new Participant("A", "1")));

        assertDoesNotThrow(() -> trip.addTransactions(List.of(transaction)));
    }

    @Test
    void settleShouldDelegateToProvidedSettler() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));
        CapturingSettler settler = new CapturingSettler(List.of(new Debt("1", "2", 5f)));

        List<Debt> result = trip.settle(settler);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).from());
        assertNotNull(settler.receivedTransactions);
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
