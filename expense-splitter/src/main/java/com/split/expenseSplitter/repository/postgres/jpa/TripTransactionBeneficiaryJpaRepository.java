package com.split.expenseSplitter.repository.postgres.jpa;

import com.split.expenseSplitter.repository.postgres.entity.TripTransactionBeneficiaryEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionBeneficiaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripTransactionBeneficiaryJpaRepository extends JpaRepository<TripTransactionBeneficiaryEntity, TripTransactionBeneficiaryId> {
    List<TripTransactionBeneficiaryEntity> findByIdTripIdAndIdTransactionIdOrderByIdBeneficiaryOrderAsc(UUID tripId, UUID transactionId);
}
