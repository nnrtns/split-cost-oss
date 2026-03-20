package com.split.expenseSplitter.service.impl;

import com.split.expenseSplitter.repository.TripRepository;
import com.split.trip.Participant;
import com.split.trip.Trip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void createTrip_shouldMapParticipantNamesToDomainParticipants() {
        Trip result = tripService.createTrip("France", List.of("Alice", "Bob"));

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).createTrip(captor.capture());
        Trip saved = captor.getValue();

        assertEquals("France", saved.getTripName());
        assertEquals(List.of("Alice", "Bob"), saved.getParticipants().stream().map(Participant::name).toList());
        assertEquals(result.getTripId(), saved.getTripId());
    }

    @Test
    void addParticipants_shouldMapNamesAndDelegate() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        trip.setTripId("trip-1");
        when(tripRepository.addParticipants(eq("trip-1"), anyList())).thenReturn(trip);

        Trip result = tripService.addParticipants("trip-1", List.of("Alice", "Bob"));

        assertEquals("trip-1", result.getTripId());
        verify(tripRepository).addParticipants(eq("trip-1"), anyList());
    }

    @Test
    void removeParticipants_shouldDelegate() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripRepository.removeParticipants("trip-1", List.of("P1"))).thenReturn(trip);

        Trip result = tripService.removeParticipants("trip-1", List.of("P1"));

        assertEquals("trip-1", result.getTripId());
        verify(tripRepository).removeParticipants("trip-1", List.of("P1"));
    }

    @Test
    void deleteTrip_shouldDelegate() {
        tripService.deleteTrip("trip-1");
        verify(tripRepository).deleteTrip("trip-1");
    }

    @Test
    void getAllTrips_shouldDelegate() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        when(tripRepository.getAllTrips()).thenReturn(List.of(trip));

        List<Trip> trips = tripService.getAllTrips();

        assertEquals(1, trips.size());
        verify(tripRepository).getAllTrips();
    }

    @Test
    void getTripById_shouldDelegate() {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        when(tripRepository.getTripById("trip-1")).thenReturn(trip);

        Trip result = tripService.getTripById("trip-1");

        assertEquals("trip-1", result.getTripId());
        verify(tripRepository).getTripById("trip-1");
    }
}
