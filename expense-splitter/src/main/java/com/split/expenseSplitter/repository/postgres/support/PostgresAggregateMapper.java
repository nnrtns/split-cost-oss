package com.split.expenseSplitter.repository.postgres.support;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.postgres.entity.*;
import com.split.expenseSplitter.repository.postgres.jpa.*;
import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.settler.Debt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostgresAggregateMapper {

    private final TripParticipantJpaRepository participantJpaRepository;
    private final TripTransactionJpaRepository transactionJpaRepository;
    private final TripTransactionBeneficiaryJpaRepository beneficiaryJpaRepository;
    private final TripCurrentSettlementJpaRepository settlementJpaRepository;

    public PostgresAggregateMapper(
            TripParticipantJpaRepository participantJpaRepository,
            TripTransactionJpaRepository transactionJpaRepository,
            TripTransactionBeneficiaryJpaRepository beneficiaryJpaRepository,
            TripCurrentSettlementJpaRepository settlementJpaRepository
    ) {
        this.participantJpaRepository = participantJpaRepository;
        this.transactionJpaRepository = transactionJpaRepository;
        this.beneficiaryJpaRepository = beneficiaryJpaRepository;
        this.settlementJpaRepository = settlementJpaRepository;
    }

    public Trip toTripAggregate(TripEntity tripEntity, boolean activeParticipantsOnly) {
        UUID tripId = tripEntity.getTripId();

        List<TripParticipantEntity> allParticipantEntities = participantJpaRepository.findByIdTripIdOrderByParticipantOrderAsc(tripId);
        if (allParticipantEntities.isEmpty()) {
            throw new ValidationException("Trip has no participants");
        }

        List<TripParticipantEntity> participantEntities = activeParticipantsOnly
                ? participantJpaRepository.findByIdTripIdAndActiveTrueOrderByParticipantOrderAsc(tripId)
                : allParticipantEntities;

        List<Participant> participantsForTrip = participantEntities.isEmpty()
                ? allParticipantEntities.stream().map(this::toParticipant).toList()
                : participantEntities.stream().map(this::toParticipant).toList();

        Map<String, Participant> allParticipantsById = allParticipantEntities.stream()
                .map(this::toParticipant)
                .collect(Collectors.toMap(Participant::participantId, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        Trip trip = new Trip(tripEntity.getTripName(), participantsForTrip);
        trip.setTripId(tripId.toString());

        List<Transaction> transactions = transactionJpaRepository.findByIdTripIdOrderByTxOrderAsc(tripId).stream()
                .map(tx -> toTransaction(tx, allParticipantsById))
                .toList();
        trip.getBalanceSheet().setTransactions(transactions);

        List<Debt> persistedSettlements = settlementJpaRepository.findByIdTripIdOrderByIdSettlementOrderAsc(tripId).stream()
                .map(this::toDebt)
                .toList();
        trip.setSettlements(persistedSettlements);

        return trip;
    }

    public Participant toParticipant(TripParticipantEntity entity) {
        return new Participant(entity.getParticipantName(), entity.getId().getParticipantId());
    }

    public Debt toDebt(TripCurrentSettlementEntity entity) {
        return new Debt(
                entity.getFromParticipantId(),
                entity.getToParticipantId(),
                entity.getAmount().floatValue()
        );
    }

    public Transaction toTransaction(TripTransactionEntity tx, Map<String, Participant> participantsById) {
        Participant spentBy = requiredParticipant(participantsById, tx.getSpentByParticipantId());
        List<Participant> beneficiaries = beneficiaryJpaRepository
                .findByIdTripIdAndIdTransactionIdOrderByIdBeneficiaryOrderAsc(tx.getId().getTripId(), tx.getId().getTransactionId())
                .stream()
                .map(b -> requiredParticipant(participantsById, b.getBeneficiaryParticipantId()))
                .toList();

        Date spentDate = Date.from(
                tx.getSpentDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        return new Transaction(
                tx.getId().getTransactionId().toString(),
                tx.getSpentAmount().floatValue(),
                spentBy,
                CATEGORY.valueOf(tx.getSpentOn()),
                SHARETYPE.valueOf(tx.getShareType()),
                spentDate,
                beneficiaries
        );
    }

    private Participant requiredParticipant(Map<String, Participant> participantsById, String participantId) {
        Participant participant = participantsById.get(participantId);
        if (participant == null) {
            throw new ValidationException("Participant not found: " + participantId);
        }
        return participant;
    }

    public BigDecimal amount(Float amount) {
        return BigDecimal.valueOf(amount.doubleValue()).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
