package com.split.expenseSplitter.repository.postgres.jpa;

import com.split.expenseSplitter.repository.postgres.entity.TripTransactionEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripTransactionJpaRepository extends JpaRepository<TripTransactionEntity, TripTransactionId> {
    List<TripTransactionEntity> findByIdTripIdOrderByTxOrderAsc(UUID tripId);
}
