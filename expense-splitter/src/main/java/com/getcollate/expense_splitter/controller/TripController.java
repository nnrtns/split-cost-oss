package com.getcollate.expense_splitter.controller;

import java.util.Map;

import com.getcollate.expense_splitter.pojo.PUTTripRequest;
import org.springframework.web.bind.annotation.*;

import com.getcollate.expense_splitter.pojo.POSTTripRequest;

@RestController()
@RequestMapping("/trip")
public class TripController {

    @PostMapping
    public @ResponseBody Map<String, Object> postTrip(
            @RequestBody POSTTripRequest tripRequest) {
        System.out.println("Trip Name: " + tripRequest);
        return null;
    }

    @PutMapping("/{tripId}")
    public @ResponseBody Map<String, Object> putTrip(
            @RequestBody PUTTripRequest tripRequest, @PathVariable String tripId) {
        System.out.println("Trip Name: " + tripRequest + " Trip Id: " + tripId);
        return null;
    }

    @GetMapping("/{tripId}/details")
    public @ResponseBody Map<String, Object> getTrip(
            @PathVariable String tripId) {
        System.out.println("Trip Id: " + tripId);
        return null;
    }

    @GetMapping("/all")
    public @ResponseBody Map<String, Object> getAllTrips() {
        System.out.println("Aum Sri Sai Ram");
        return null;
    }
}