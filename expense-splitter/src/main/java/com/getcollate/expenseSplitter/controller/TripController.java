package com.getcollate.expenseSplitter.controller;

import java.util.List;
import java.util.Map;

import com.getcollate.expenseSplitter.pojo.PUTTripRequest;
import com.getcollate.expenseSplitter.service.TripService;
import com.getcollate.expenseSplitter.exception.DuplicateInsertionException;
import com.getcollate.expenseSplitter.exception.ValidationException;
import com.getcollate.trip.Trip;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.getcollate.expenseSplitter.pojo.POSTTripRequest;

@RestController()
@RequestMapping("/trip")
public class TripController {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    private final TripService service;

    public TripController(TripService tripService) {
        this.service = tripService;
    }

    @PostMapping
    public @ResponseBody ResponseEntity<Map<String, Object>> postTrip(
            @Valid @RequestBody POSTTripRequest tripRequest) {
        Trip response = service.createTrip(tripRequest.getName(), tripRequest.getParticipants());
        return ResponseEntity.ok().body(Map.of("tripId", response.getTripId(), "tripName", response.getTripName(), "participants", response.getParticipants()));
    }

    @PutMapping("/{tripId}")
    public @ResponseBody ResponseEntity<Map<String, Object>> putTrip(
            @Valid @RequestBody PUTTripRequest tripRequest, @PathVariable String tripId) {
        logger.info("Trip Name: " + tripRequest + " Trip Id: " + tripId);
        service.addParticipants(tripId, tripRequest.getAddParticipants());
        Trip response = service.removeParticipants(tripId, tripRequest.getRemoveParticipants());
        return ResponseEntity.ok().body(Map.of("tripId", response.getTripId(), "tripName", response.getTripName(), "participants", response.getParticipants()));
//        return null;
    }

    @GetMapping("/{tripId}/details")
    public @ResponseBody ResponseEntity<Map<String, Object>> getTrip(
            @PathVariable String tripId) {
        logger.info("Trip Id: " + tripId);
        Trip response = service.getTripById(tripId);
        return ResponseEntity.ok().body(Map.of("tripId", response.getTripId(), "tripName", response.getTripName(), "participants", response.getParticipants()));
    }

    @GetMapping("/all")
    public @ResponseBody ResponseEntity<List<Map<String, Object>>> getAllTrips() {
        logger.info("Aum Sri Sai Ram");
        List<Trip> trips = service.getAllTrips();
        return ResponseEntity.ok().body(
                trips.stream().map((Trip response) -> Map.of("tripId", response.getTripId(), "tripName", response.getTripName(), "participants", response.getParticipants())).toList()
        );
    }

    @DeleteMapping("/{tripId}")
    public @ResponseBody ResponseEntity<Map<String, Object>> deleteTrip(@PathVariable String tripId) {
        logger.info("Aum Sri Sai Ram");
        service.deleteTrip(tripId);
        return ResponseEntity.ok().body(Map.of("tripId", tripId));
    }
}