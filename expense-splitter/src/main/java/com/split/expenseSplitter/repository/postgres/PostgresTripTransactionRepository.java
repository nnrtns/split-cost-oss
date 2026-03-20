package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.TripTransactionReporitory;
import com.split.expenseSplitter.repository.postgres.entity.TripTransactionBeneficiaryEntity;
import com.split.expenseSplitter.repository.postgres.entity.TripTransactionEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionBeneficiaryId;
import com.split.expenseSplitter.repository.postgres.entity.id.TripTransactionId;
import com.split.expenseSplitter.repository.postgres.jpa.TripCurrentSettlementJpaRepository;
import com.split.expenseSplitter.repository.postgres.jpa.TripJpaRepository;
import com.split.expenseSplitter.repository.postgres.jpa.TripTransactionBeneficiaryJpaRepository;
import com.split.expenseSplitter.repository.postgres.jpa.TripTransactionJpaRepository;
import com.split.expenseSplitter.repository.postgres.support.PostgresAggregateMapper;
import com.split.trip.Trip;
import com.split.trip.Participant;
import com.split.trip.accounts.Transaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
@Primary
@Transactional
public class PostgresTripTransactionRepository implements TripTransactionReporitory {

    private final TripJpaRepository tripJpaRepository;
    private final TripTransactionJpaRepository transactionJpaRepository;
    private final TripTransactionBeneficiaryJpaRepository beneficiaryJpaRepository;
    private final TripCurrentSettlementJpaRepository settlementJpaRepository;
    private final PostgresAggregateMapper mapper;

    public PostgresTripTransactionRepository(
            TripJpaRepository tripJpaRepository,
            TripTransactionJpaRepository transactionJpaRepository,
            TripTransactionBeneficiaryJpaRepository beneficiaryJpaRepository,
            TripCurrentSettlementJpaRepository settlementJpaRepository,
            PostgresAggregateMapper mapper
    ) {
        this.tripJpaRepository = tripJpaRepository;
        this.transactionJpaRepository = transactionJpaRepository;
        this.beneficiaryJpaRepository = beneficiaryJpaRepository;
        this.settlementJpaRepository = settlementJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(String tripId) {
        UUID tripUuid = parseTripId(tripId);
        ensureTripExists(tripUuid);
        Trip trip = mapper.toTripAggregate(tripJpaRepository.getReferenceById(tripUuid), true);
        return trip.getBalanceSheet().getTransactions();
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransaction(String tripId, String transactionId) {
        UUID tripUuid = parseTripId(tripId);
        TripTransactionEntity entity = transactionJpaRepository.findById(new TripTransactionId(tripUuid, UUID.fromString(transactionId)))
                .orElseThrow(() -> new ValidationException("Transaction not found"));
        Trip trip = mapper.toTripAggregate(tripJpaRepository.getReferenceById(tripUuid), true);
        return trip.getBalanceSheet().getTransactions().stream()
                .filter(t -> t.transactionId().equals(entity.getId().getTransactionId().toString()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Transaction not found"));
    }

    @Override
    public void deleteTransaction(String tripId, String transactionId) {
        UUID tripUuid = parseTripId(tripId);
        TripTransactionId id = new TripTransactionId(tripUuid, UUID.fromString(transactionId));
        if (!transactionJpaRepository.existsById(id)) {
            throw new ValidationException("Transaction not found to be deleted");
        }
        transactionJpaRepository.deleteById(id);
        settlementJpaRepository.deleteByIdTripId(tripUuid);
    }

    @Override
    public boolean createTransaction(String tripId, List<Transaction> transactions) {
        UUID tripUuid = parseTripId(tripId);
        ensureTripExists(tripUuid);

        for (Transaction transaction : transactions) {
            TripTransactionId txId = new TripTransactionId(tripUuid, UUID.fromString(transaction.transactionId()));
            if (transactionJpaRepository.existsById(txId)) {
                throw new ValidationException("Duplicate transactions found: [" + transaction.transactionId() + "]");
            }
        }

        for (Transaction transaction : transactions) {
            transactionJpaRepository.save(toTransactionEntity(tripUuid, transaction));
            List<TripTransactionBeneficiaryEntity> beneficiaries = toBeneficiaryEntities(tripUuid, transaction);
            beneficiaryJpaRepository.saveAll(beneficiaries);
        }

        settlementJpaRepository.deleteByIdTripId(tripUuid);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Trip getTripForTransaction(String tripId) throws ValidationException {
        UUID tripUuid = parseTripId(tripId);
        return mapper.toTripAggregate(
                tripJpaRepository.findById(tripUuid).orElseThrow(() -> new ValidationException("Trip not found")),
                true
        );
    }

    private void ensureTripExists(UUID tripId) {
        if (!tripJpaRepository.existsById(tripId)) {
            throw new ValidationException("Trip not found");
        }
    }

    private UUID parseTripId(String tripId) {
        return UUID.fromString(tripId);
    }

    private TripTransactionEntity toTransactionEntity(UUID tripId, Transaction transaction) {
        TripTransactionEntity entity = new TripTransactionEntity();
        entity.setId(new TripTransactionId(tripId, UUID.fromString(transaction.transactionId())));
        entity.setSpentAmount(mapper.amount(transaction.spentAmount()));
        entity.setSpentByParticipantId(transaction.spentBy().participantId());
        entity.setSpentOn(transaction.spentOn().name());
        entity.setShareType(transaction.shareType().name());
        entity.setSpentDate(toLocalDate(transaction.spentDate()));
        return entity;
    }

    private List<TripTransactionBeneficiaryEntity> toBeneficiaryEntities(UUID tripId, Transaction transaction) {
        UUID transactionId = UUID.fromString(transaction.transactionId());
        List<Participant> beneficiaries = transaction.benefittedBy();
        return java.util.stream.IntStream.range(0, beneficiaries.size())
                .mapToObj(i -> {
                    TripTransactionBeneficiaryEntity entity = new TripTransactionBeneficiaryEntity();
                    entity.setId(new TripTransactionBeneficiaryId(tripId, transactionId, i));
                    entity.setBeneficiaryParticipantId(beneficiaries.get(i).participantId());
                    return entity;
                })
                .toList();
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
