package com.getcollate.expenseSplitter.service;

import com.getcollate.expenseSplitter.exception.DuplicateInsertionException;
import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.trip.Trip;

import java.util.List;

public interface TripService {
    Trip createTrip(String name, List<String> participants) throws ValidationException, DuplicateInsertionException;
    Trip addParticipants(String tripId, List<String> participants) throws ValidationException, DuplicateInsertionException;
    Trip removeParticipants(String tripId, List<String> participants) throws ValidationException;
    void deleteTrip(String tripId) throws ValidationException;
    List<Trip> getAllTrips() throws ValidationException;
    Trip getTripById(String tripId) throws ValidationException;
}
