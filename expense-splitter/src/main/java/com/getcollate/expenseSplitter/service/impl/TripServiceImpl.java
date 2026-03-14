package com.getcollate.expenseSplitter.service.impl;

import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.expenseSplitter.repository.TripRepository;
import com.getcollate.expenseSplitter.service.TripService;
import com.getcollate.trip.Participant;
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
        List<Participant> participantList = participants.stream()
                .map(Participant::new)
                .toList();
        Trip trip = new Trip(name, participantList);
        tripRepository.createTrip(trip);
        return trip;
    }

    @Override
    public Trip addParticipants(String tripId, List<String> participants) {
        List<Participant> participantList = participants.stream()
                .map(Participant::new)
                .toList();
        Trip trip = tripRepository.addParticipants(tripId, participantList);
        return trip;
    }

    @Override
    public Trip removeParticipants(String tripId, List<String> participants) {
        return tripRepository.removeParticipants(tripId, participants);
    }

    @Override
    public void deleteTrip(String tripId) {
        tripRepository.deleteTrip(tripId);
    }

    @Override
    public List<Trip> getAllTrips() throws ValidationException {
        return tripRepository.getAllTrips();
    }

    @Override
    public Trip getTripById(String tripId) throws ValidationException {
        return tripRepository.getTripById(tripId);
    }
}
