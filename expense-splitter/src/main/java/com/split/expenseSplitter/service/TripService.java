package com.split.expenseSplitter.service;

import com.split.expenseSplitter.exception.DuplicateInsertionException;
import com.split.expenseSplitter.exception.ValidationException;
import com.split.trip.Trip;

import java.util.List;

public interface TripService {
    Trip createTrip(String name, List<String> participants) throws ValidationException, DuplicateInsertionException;
    Trip addParticipants(String tripId, List<String> participants) throws ValidationException, DuplicateInsertionException;
    Trip removeParticipants(String tripId, List<String> participants) throws ValidationException;
    void deleteTrip(String tripId) throws ValidationException;
    List<Trip> getAllTrips() throws ValidationException;
    Trip getTripById(String tripId) throws ValidationException;
}
