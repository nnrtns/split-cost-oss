package com.split.expenseSplitter.repository.postgres.entity;

import com.split.expenseSplitter.repository.postgres.entity.id.TripParticipantId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trip_participant", schema = "expense_splitter")
public class TripParticipantEntity {

    @EmbeddedId
    private TripParticipantId id;

    @Column(name = "participant_name", nullable = false)
    private String participantName;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "participant_order", insertable = false, updatable = false)
    private Long participantOrder;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "removed_at")
    private OffsetDateTime removedAt;

    public TripParticipantId getId() {
        return id;
    }

    public void setId(TripParticipantId id) {
        this.id = id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getParticipantOrder() {
        return participantOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(OffsetDateTime removedAt) {
        this.removedAt = removedAt;
    }
}
