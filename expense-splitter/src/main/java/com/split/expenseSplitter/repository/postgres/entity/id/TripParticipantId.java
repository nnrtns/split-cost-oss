package com.split.expenseSplitter.repository.postgres.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripParticipantId implements Serializable {

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "participant_id", nullable = false, length = 64)
    private String participantId;

    public TripParticipantId() {
    }

    public TripParticipantId(UUID tripId, String participantId) {
        this.tripId = tripId;
        this.participantId = participantId;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripParticipantId that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(participantId, that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, participantId);
    }
}
