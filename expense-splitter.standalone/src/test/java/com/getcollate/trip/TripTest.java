package com.getcollate.trip;

import com.getcollate.trip.accounts.Transaction;
import com.getcollate.trip.accounts.settler.Debt;
import com.getcollate.trip.accounts.settler.Settler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.getcollate.trip.accounts.CATEGORY.FOOD;
import static com.getcollate.trip.accounts.SHARETYPE.EQUAL;
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
    void getParticipantShouldReturnParticipantByName() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1"), new Participant("B", "2")));

        Participant participant = trip.getParticipant("A");

        assertEquals("A", participant.name());
        assertEquals("1", participant.participantId());
    }

    @Test
    void getParticipantShouldThrowWhenNameDoesNotExist() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trip.getParticipant("X"));

        assertTrue(ex.getMessage().contains("Participant not found in the trip"));
    }

    @Test
    void addParticipantsShouldMakeNewParticipantRetrievable() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1")));

        trip.addParticipants(List.of(new Participant("B", "2")));

        assertEquals("B", trip.getParticipant("B").name());
    }

    @Test
    void removeParticipantsShouldRemoveParticipantFromLookup() {
        Trip trip = new Trip("Bangkok", List.of(new Participant("A", "1"), new Participant("B", "2")));

        trip.removeParticipants(List.of("B"));

        assertThrows(RuntimeException.class, () -> trip.getParticipant("B"));
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
        CapturingSettler settler = new CapturingSettler(List.of(new Debt("A", "B", 5f)));

        List<Debt> result = trip.settle(settler);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).from());
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
