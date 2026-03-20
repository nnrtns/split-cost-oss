package com.split.expenseSplitter.repository.postgres.entity;

import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionBeneficiaryId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "trip_transaction_beneficiary", schema = "expense_splitter")
public class TripTransactionBeneficiaryEntity {

    @EmbeddedId
    private TripTransactionBeneficiaryId id;

    @Column(name = "beneficiary_participant_id", nullable = false)
    private String beneficiaryParticipantId;

    public TripTransactionBeneficiaryId getId() {
        return id;
    }

    public void setId(TripTransactionBeneficiaryId id) {
        this.id = id;
    }

    public String getBeneficiaryParticipantId() {
        return beneficiaryParticipantId;
    }

    public void setBeneficiaryParticipantId(String beneficiaryParticipantId) {
        this.beneficiaryParticipantId = beneficiaryParticipantId;
    }
}
