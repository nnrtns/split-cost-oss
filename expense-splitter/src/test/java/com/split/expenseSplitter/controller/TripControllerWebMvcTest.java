package com.split.expenseSplitter.controller;

import com.split.expenseSplitter.exception.GlobalExceptionHandler;
import com.split.expenseSplitter.exception.GlobalValidationExceptionHandler;
import com.split.expenseSplitter.exception.DuplicateInsertionException;
import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.service.TripService;
import com.split.trip.Participant;
import com.split.trip.Trip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
@Import({GlobalExceptionHandler.class, GlobalValidationExceptionHandler.class})
class TripControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @Test
    void postTrip_shouldReturnCreatedTrip() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        trip.setTripId("trip-1");
        given(tripService.createTrip(eq("France"), anyList())).willReturn(trip);

        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "France",
                                  "participants": ["Alice", "Bob"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value("trip-1"))
                .andExpect(jsonPath("$.tripName").value("France"))
                .andExpect(jsonPath("$.participants.length()").value(2));
    }

    @Test
    void postTrip_shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "participants": ["Alice"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Trip name cannot be blank."));
    }

    @Test
    void postTrip_shouldReturn400WhenParticipantsAreEmpty() throws Exception {
        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "France",
                                  "participants": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.participants").value("Trip participants cannot be blank."));
    }

    @Test
    void postTrip_shouldReturn400WhenParticipantNameIsBlank() throws Exception {
        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "France",
                                  "participants": ["Alice", ""]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['participants[1]']").value("null cannot be a Participant."));
    }

    @Test
    void postTrip_shouldMapValidationExceptionTo422() throws Exception {
        given(tripService.createTrip(eq("France"), anyList())).willThrow(new ValidationException("Trip not found"));

        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "France",
                                  "participants": ["Alice"]
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Trip not found"));
    }

    @Test
    void addParticipants_shouldReturnUpdatedTrip() throws Exception {
        Trip trip = new Trip("France", List.of(
                new Participant("Alice", "P1"),
                new Participant("Bob", "P2"),
                new Participant("Smith", "P3")
        ));
        trip.setTripId("trip-1");
        given(tripService.addParticipants(eq("trip-1"), anyList())).willReturn(trip);

        mockMvc.perform(post("/trip/trip-1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participants": ["Smith"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants.length()").value(3));
    }

    @Test
    void addParticipants_shouldReturn400WhenListIsEmpty() throws Exception {
        mockMvc.perform(post("/trip/trip-1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participants": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.participants").value("You must provide at least one participant."));
    }

    @Test
    void addParticipants_shouldMapDuplicateInsertionTo409() throws Exception {
        given(tripService.addParticipants(eq("trip-1"), anyList())).willThrow(new DuplicateInsertionException("duplicate"));

        mockMvc.perform(post("/trip/trip-1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participants": ["Smith"]
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("duplicate"));
    }

    @Test
    void removeParticipants_shouldReturnUpdatedTrip() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1"), new Participant("Bob", "P2")));
        trip.setTripId("trip-1");
        given(tripService.removeParticipants(eq("trip-1"), anyList())).willReturn(trip);

        mockMvc.perform(delete("/trip/trip-1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participants": ["P3"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value("trip-1"));
    }

    @Test
    void getTrip_shouldReturnTripDetails() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        given(tripService.getTripById("trip-1")).willReturn(trip);

        mockMvc.perform(get("/trip/trip-1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripName").value("France"));
    }

    @Test
    void getTrip_shouldMapRuntimeExceptionTo422() throws Exception {
        given(tripService.getTripById("trip-1")).willThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/trip/trip-1/details"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("boom"));
    }

    @Test
    void getAllTrips_shouldReturnTrips() throws Exception {
        Trip trip = new Trip("France", List.of(new Participant("Alice", "P1")));
        trip.setTripId("trip-1");
        given(tripService.getAllTrips()).willReturn(List.of(trip));

        mockMvc.perform(get("/trip/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripId").value("trip-1"));
    }

    @Test
    void deleteTrip_shouldReturnDeletedTripId() throws Exception {
        mockMvc.perform(delete("/trip/trip-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value("trip-1"));
    }
}