package com.split.expenseSplitter.repository.postgres.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripTransactionBeneficiaryId implements Serializable {

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "beneficiary_order", nullable = false)
    private Integer beneficiaryOrder;

    public TripTransactionBeneficiaryId() {
    }

    public TripTransactionBeneficiaryId(UUID tripId, UUID transactionId, Integer beneficiaryOrder) {
        this.tripId = tripId;
        this.transactionId = transactionId;
        this.beneficiaryOrder = beneficiaryOrder;
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

    public Integer getBeneficiaryOrder() {
        return beneficiaryOrder;
    }

    public void setBeneficiaryOrder(Integer beneficiaryOrder) {
        this.beneficiaryOrder = beneficiaryOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripTransactionBeneficiaryId that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(transactionId, that.transactionId) && Objects.equals(beneficiaryOrder, that.beneficiaryOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, transactionId, beneficiaryOrder);
    }
}
