package com.split.expenseSplitter.repository.postgres.entity;

import com.split.expenseSplitter.repository.postgres.entity.id.TripCurrentSettlementId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trip_current_settlement", schema = "expense_splitter")
public class TripCurrentSettlementEntity {

    @EmbeddedId
    private TripCurrentSettlementId id;

    @Column(name = "from_participant_id", nullable = false)
    private String fromParticipantId;

    @Column(name = "to_participant_id", nullable = false)
    private String toParticipantId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public TripCurrentSettlementId getId() {
        return id;
    }

    public void setId(TripCurrentSettlementId id) {
        this.id = id;
    }

    public String getFromParticipantId() {
        return fromParticipantId;
    }

    public void setFromParticipantId(String fromParticipantId) {
        this.fromParticipantId = fromParticipantId;
    }

    public String getToParticipantId() {
        return toParticipantId;
    }

    public void setToParticipantId(String toParticipantId) {
        this.toParticipantId = toParticipantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
