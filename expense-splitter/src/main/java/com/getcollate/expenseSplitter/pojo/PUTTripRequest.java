package com.getcollate.expenseSplitter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PUTTripRequest {

    @NotEmpty(message = "You must provide at least one participant.")
    List<@NotBlank(message = "Cannot leave participants to be blank.")  String> participants;

    public String toString() {
        return "PUTTripRequest{" + "participants=" + participants + '}';
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
