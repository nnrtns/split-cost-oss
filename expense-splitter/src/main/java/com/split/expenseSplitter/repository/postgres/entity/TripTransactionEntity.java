package com.split.expenseSplitter.repository.postgres.entity;

import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trip_transaction", schema = "expense_splitter")
public class TripTransactionEntity {

    @EmbeddedId
    private TripTransactionId id;

    @Column(name = "tx_order", insertable = false, updatable = false)
    private Long txOrder;

    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount;

    @Column(name = "spent_by_participant_id", nullable = false)
    private String spentByParticipantId;

    @Column(name = "spent_on", nullable = false)
    private String spentOn;

    @Column(name = "share_type", nullable = false)
    private String shareType;

    @Column(name = "spent_date", nullable = false)
    private LocalDate spentDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public TripTransactionId getId() {
        return id;
    }

    public void setId(TripTransactionId id) {
        this.id = id;
    }

    public Long getTxOrder() {
        return txOrder;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getSpentByParticipantId() {
        return spentByParticipantId;
    }

    public void setSpentByParticipantId(String spentByParticipantId) {
        this.spentByParticipantId = spentByParticipantId;
    }

    public String getSpentOn() {
        return spentOn;
    }

    public void setSpentOn(String spentOn) {
        this.spentOn = spentOn;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public LocalDate getSpentDate() {
        return spentDate;
    }

    public void setSpentDate(LocalDate spentDate) {
        this.spentDate = spentDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
