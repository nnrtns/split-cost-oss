package com.split.expenseSplitter.repository.postgres.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripCurrentSettlementId implements Serializable {

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "settlement_order", nullable = false)
    private Integer settlementOrder;

    public TripCurrentSettlementId() {
    }

    public TripCurrentSettlementId(UUID tripId, Integer settlementOrder) {
        this.tripId = tripId;
        this.settlementOrder = settlementOrder;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

    public Integer getSettlementOrder() {
        return settlementOrder;
    }

    public void setSettlementOrder(Integer settlementOrder) {
        this.settlementOrder = settlementOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripCurrentSettlementId that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(settlementOrder, that.settlementOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, settlementOrder);
    }
}
