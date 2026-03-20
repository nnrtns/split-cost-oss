package com.split.expenseSplitter.repository.postgres.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripTransactionId implements Serializable {

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    public TripTransactionId() {
    }

    public TripTransactionId(UUID tripId, UUID transactionId) {
        this.tripId = tripId;
        this.transactionId = transactionId;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripTransactionId that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, transactionId);
    }
}
