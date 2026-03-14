package com.getcollate.expenseSplitter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;

import java.util.List;

public class PUTTripRequest {

    List<String> addParticipants;
    List<String> removeParticipants;

    public List<String> getAddParticipants() {
        return addParticipants;
    }

    public List<String> getRemoveParticipants() {
        return removeParticipants;
    }

    public void setRemoveParticipants(List<String> removeParticipants) {
        this.removeParticipants = removeParticipants;
    }

    public void setAddParticipants(List<String> addParticipants) {
        this.addParticipants = addParticipants;
    }

    public String toString() {
        return "PUTTripRequest{" + "addParticipants=" + addParticipants + ", removeParticipants=" + removeParticipants + '}';
    }

    @JsonIgnore // Prevents this boolean from accidentally showing up in JSON responses
    @AssertTrue(message = "You must provide either addParticipants or removeParticipants. Both cannot be empty.")
    public boolean isAtLeastOneParticipantrProvided() {

        boolean hasAddParticipants = (addParticipants != null && !addParticipants.isEmpty());
        boolean hasRemoveParticipants = (removeParticipants != null && !removeParticipants.isEmpty());

        // Returns true if AT LEAST ONE of them has data.
        // Returns false if BOTH are null/empty (triggering the validation error!)
        return hasAddParticipants || hasRemoveParticipants;
    }
}
