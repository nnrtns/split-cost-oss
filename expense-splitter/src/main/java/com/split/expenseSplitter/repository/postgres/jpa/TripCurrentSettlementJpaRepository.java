package com.split.expenseSplitter.repository.postgres.jpa;

import com.split.expenseSplitter.repository.postgres.entity.TripCurrentSettlementEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripCurrentSettlementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripCurrentSettlementJpaRepository extends JpaRepository<TripCurrentSettlementEntity, TripCurrentSettlementId> {
    List<TripCurrentSettlementEntity> findByIdTripIdOrderByIdSettlementOrderAsc(UUID tripId);
    void deleteByIdTripId(UUID tripId);
}
