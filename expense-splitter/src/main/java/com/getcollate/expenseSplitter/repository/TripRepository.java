package com.getcollate.expenseSplitter.repository;

import com.getcollate.trip.Participant;
import com.getcollate.trip.Trip;

import java.util.List;

public interface TripRepository {
    boolean createTrip(Trip trip);
    Trip addParticipants(String tripId, List<Participant> participants);
    Trip removeParticipants(String tripId, List<String> participants);
    void deleteTrip(String tripId);
    List<Trip> getAllTrips();
    Trip getTripById(String tripId);
}
