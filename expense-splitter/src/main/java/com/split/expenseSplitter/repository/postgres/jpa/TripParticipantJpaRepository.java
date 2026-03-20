package com.split.expenseSplitter.repository.postgres.jpa;

import com.split.expenseSplitter.repository.postgres.entity.TripParticipantEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripParticipantJpaRepository extends JpaRepository<TripParticipantEntity, TripParticipantId> {
    List<TripParticipantEntity> findByIdTripIdOrderByParticipantOrderAsc(UUID tripId);
    List<TripParticipantEntity> findByIdTripIdAndActiveTrueOrderByParticipantOrderAsc(UUID tripId);
    Optional<TripParticipantEntity> findByIdTripIdAndParticipantName(UUID tripId, String participantName);
    boolean existsByIdTripIdAndActiveTrue(UUID tripId);
}
