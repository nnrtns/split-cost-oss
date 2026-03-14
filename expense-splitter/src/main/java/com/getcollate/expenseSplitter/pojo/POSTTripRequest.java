package com.getcollate.expenseSplitter.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class POSTTripRequest {
    @NotBlank(message = "Trip name cannot be blank.")
    String name;

    @NotNull(message = "Trip participants cannot be null.")
    @NotEmpty(message = "Trip participants cannot be blank.")
    List<@NotBlank(message = "null cannot be a Participant.") String> participants;

    @java.lang.Override
    public java.lang.String toString() {
        return "POSTTripRequest{" +
                "tripName='" + name + '\'' +
                ", participants=" + participants +
                '}';
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
