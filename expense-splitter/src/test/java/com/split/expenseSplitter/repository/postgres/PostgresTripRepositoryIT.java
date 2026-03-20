package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.postgres.jpa.TripParticipantJpaRepository;
import com.split.trip.Participant;
import com.split.trip.Trip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostgresTripRepositoryIT extends AbstractPostgresRepositoryIT {

    @Autowired
    private PostgresTripRepository repository;

    @Autowired
    private TripParticipantJpaRepository participantJpaRepository;

    @Test
    void createTrip_andGetTripById_shouldRoundTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        repository.createTrip(trip);

        Trip loaded = repository.getTripById(trip.getTripId());

        assertEquals(trip.getTripId(), loaded.getTripId());
        assertEquals("France", loaded.getTripName());
        assertEquals(2, loaded.getParticipants().size());
    }

    @Test
    void createTrip_shouldRejectDuplicateTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class, () -> repository.createTrip(trip));
        assertEquals("Trip already exists", ex.getMessage());
    }

    @Test
    void addParticipants_shouldPersistAndReturnUpdatedTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        Trip updated = repository.addParticipants(trip.getTripId(), List.of(new Participant("Bob", "P2")));

        assertEquals(2, updated.getParticipants().size());
        assertEquals(List.of("Alice", "Bob"), updated.getParticipants().stream().map(Participant::name).toList());
    }

    @Test
    void addParticipants_shouldRejectDuplicateParticipantName() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> repository.addParticipants(trip.getTripId(), List.of(new Participant("Alice", "P2"))));

        assertTrue(ex.getMessage().contains("Participant already exists"));
    }

    @Test
    void addParticipants_shouldRejectDuplicateParticipantId() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> repository.addParticipants(trip.getTripId(), List.of(new Participant("Bob", "P1"))));

        assertTrue(ex.getMessage().contains("Participant already exists"));
    }

    @Test
    void removeParticipants_shouldSoftDeleteAndHideParticipantFromReturnedTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        repository.createTrip(trip);

        Trip updated = repository.removeParticipants(trip.getTripId(), List.of("P2"));

        assertEquals(1, updated.getParticipants().size());
        assertEquals("P1", updated.getParticipants().getFirst().participantId());
        assertFalse(participantJpaRepository.findByIdTripIdOrderByParticipantOrderAsc(java.util.UUID.fromString(trip.getTripId()))
                .stream().filter(p -> p.getId().getParticipantId().equals("P2")).findFirst().orElseThrow().isActive());
    }

    @Test
    void removeParticipants_shouldRejectMissingParticipant() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> repository.removeParticipants(trip.getTripId(), List.of("P9")));

        assertTrue(ex.getMessage().contains("Participant not found"));
    }

    @Test
    void removeParticipants_shouldRejectAlreadyInactiveParticipant() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        repository.createTrip(trip);
        repository.removeParticipants(trip.getTripId(), List.of("P2"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> repository.removeParticipants(trip.getTripId(), List.of("P2")));

        assertTrue(ex.getMessage().contains("Participant not found"));
    }

    @Test
    void getAllTrips_shouldRejectWhenNoTripsExist() {
        ValidationException ex = assertThrows(ValidationException.class, repository::getAllTrips);
        assertEquals("No trips found", ex.getMessage());
    }

    @Test
    void deleteTrip_shouldRemoveTrip() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        repository.createTrip(trip);

        repository.deleteTrip(trip.getTripId());

        ValidationException ex = assertThrows(ValidationException.class, () -> repository.getTripById(trip.getTripId()));
        assertEquals("Trip not found", ex.getMessage());
    }
}
