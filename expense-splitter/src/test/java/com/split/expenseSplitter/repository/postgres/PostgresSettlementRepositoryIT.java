package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.settler.Debt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresSettlementRepositoryIT extends AbstractPostgresRepositoryIT {

    @Autowired
    private PostgresTripRepository tripRepository;

    @Autowired
    private PostgresSettlementRepository settlementRepository;

    @Autowired
    private com.split.expenseSplitter.repository.postgres.jpa.TripCurrentSettlementJpaRepository settlementJpaRepository;

    @Test
    void persistSettlement_shouldBeVisibleFromLoadedTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);

        settlementRepository.persistsettlement(trip.getTripId(), List.of(new Debt("P2", "P1", 10.5f)));

        Trip loaded = settlementRepository.getTrip(trip.getTripId());
        assertEquals(1, loaded.getSettlements().size());
        assertEquals("P2", loaded.getSettlements().getFirst().from());
        assertEquals("P1", loaded.getSettlements().getFirst().to());
        assertEquals(10.5f, loaded.getSettlements().getFirst().amount());
    }

    @Test
    void persistSettlement_shouldRewritePreviousSnapshotNotAppend() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);

        settlementRepository.persistsettlement(trip.getTripId(), List.of(new Debt("P2", "P1", 10f), new Debt("P3", "P1", 5f)));
        settlementRepository.persistsettlement(trip.getTripId(), List.of(new Debt("P2", "P1", 12f)));

        List<com.split.expenseSplitter.repository.postgres.entity.TripCurrentSettlementEntity> rows = settlementJpaRepository.findByIdTripIdOrderByIdSettlementOrderAsc(UUID.fromString(trip.getTripId()));
        assertEquals(1, rows.size());
        assertEquals("P2", rows.getFirst().getFromParticipantId());
    }

    @Test
    void persistSettlement_withEmptyList_shouldClearPreviousSnapshot() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        tripRepository.createTrip(trip);

        settlementRepository.persistsettlement(trip.getTripId(), List.of(new Debt("P2", "P1", 10f)));
        settlementRepository.persistsettlement(trip.getTripId(), List.of());

        assertTrue(settlementJpaRepository.findByIdTripIdOrderByIdSettlementOrderAsc(UUID.fromString(trip.getTripId())).isEmpty());
    }

    @Test
    void persistSettlement_shouldRejectMissingTrip() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> settlementRepository.persistsettlement(UUID.randomUUID().toString(), List.of(new Debt("P2", "P1", 1f))));

        assertEquals("You are trying to update a trip that does not exist", ex.getMessage());
    }

    @Test
    void getTrip_shouldRejectMissingTrip() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> settlementRepository.getTrip(UUID.randomUUID().toString()));

        assertEquals("Trip not found", ex.getMessage());
    }
}
