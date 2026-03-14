package com.getcollate.expenseSplitter.repository;

import com.getcollate.trip.Trip;

import java.util.List;

public interface TripRepository {
    boolean createTrip(Trip trip);
    Trip updateTrip(String tripId, Trip trip);
    void deleteTrip(String tripId);
    List<Trip> getAllTrips();
    Trip getTripById(String tripId);
}
