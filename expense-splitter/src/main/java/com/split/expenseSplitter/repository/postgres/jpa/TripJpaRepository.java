package com.split.expenseSplitter.repository.postgres.jpa;

import com.split.expenseSplitter.repository.postgres.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripJpaRepository extends JpaRepository<TripEntity, UUID> {
}
