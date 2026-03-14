package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.expenseSplitter.repository.TripRepository;
import com.getcollate.expenseSplitter.service.TripService;
import com.getcollate.trip.Trip;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripServiceImpl implements TripService {

    TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip createTrip(String name, List<String> participants) {
        throw new ValidationException("This is a validation error");
//        return null;
    }

    @Override
    public Trip addParticipants(String tripId, List<String> participants) {
        return null;
    }

    @Override
    public Trip removeParticipants(String tripId, List<String> participants) {
        return null;
    }

    @Override
    public void deleteTrip(String tripId) {

    }

    @Override
    public List<Trip> getAllTrips() throws ValidationException {
        return List.of();
    }

    @Override
    public Trip getTripById(String tripId) throws ValidationException {
        return null;
    }
}
