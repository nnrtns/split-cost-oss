package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.SettlementRepository;
import com.split.expenseSplitter.repository.postgres.entity.TripCurrentSettlementEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripCurrentSettlementId;
import com.split.expenseSplitter.repository.postgres.jpa.TripCurrentSettlementJpaRepository;
import com.split.expenseSplitter.repository.postgres.jpa.TripJpaRepository;
import com.split.expenseSplitter.repository.postgres.support.PostgresAggregateMapper;
import com.split.trip.Trip;
import com.split.trip.accounts.settler.Debt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Primary
@Transactional
public class PostgresSettlementRepository implements SettlementRepository {

    private final TripJpaRepository tripJpaRepository;
    private final TripCurrentSettlementJpaRepository settlementJpaRepository;
    private final PostgresAggregateMapper mapper;

    public PostgresSettlementRepository(
            TripJpaRepository tripJpaRepository,
            TripCurrentSettlementJpaRepository settlementJpaRepository,
            PostgresAggregateMapper mapper
    ) {
        this.tripJpaRepository = tripJpaRepository;
        this.settlementJpaRepository = settlementJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean persistsettlement(String tripId, List<Debt> settlements) {
        UUID tripUuid = UUID.fromString(tripId);
        if (!tripJpaRepository.existsById(tripUuid)) {
            throw new ValidationException("You are trying to update a trip that does not exist");
        }

        settlementJpaRepository.deleteByIdTripId(tripUuid);

        List<TripCurrentSettlementEntity> rows = new ArrayList<>();
        for (int i = 0; i < settlements.size(); i++) {
            Debt debt = settlements.get(i);
            TripCurrentSettlementEntity entity = new TripCurrentSettlementEntity();
            entity.setId(new TripCurrentSettlementId(tripUuid, i));
            entity.setFromParticipantId(debt.from());
            entity.setToParticipantId(debt.to());
            entity.setAmount(mapper.amount(debt.amount()));
            rows.add(entity);
        }
        settlementJpaRepository.saveAll(rows);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Trip getTrip(String tripId) {
        UUID tripUuid = UUID.fromString(tripId);
        return mapper.toTripAggregate(
                tripJpaRepository.findById(tripUuid).orElseThrow(() -> new ValidationException("Trip not found")),
                true
        );
    }
}
