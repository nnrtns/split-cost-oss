package com.split.expenseSplitter.repository.postgres;

import com.split.expenseSplitter.exception.ValidationException;
import com.split.expenseSplitter.repository.TripRepository;
import com.split.expenseSplitter.repository.postgres.entity.TripEntity;
import com.split.expenseSplitter.repository.postgres.entity.TripParticipantEntity;
import com.split.expenseSplitter.repository.postgres.entity.id.TripParticipantId;
import com.split.expenseSplitter.repository.postgres.jpa.TripJpaRepository;
import com.split.expenseSplitter.repository.postgres.jpa.TripParticipantJpaRepository;
import com.split.expenseSplitter.repository.postgres.support.PostgresAggregateMapper;
import com.split.trip.Participant;
import com.split.trip.Trip;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Primary
@Transactional
public class PostgresTripRepository implements TripRepository {

    private final TripJpaRepository tripJpaRepository;
    private final TripParticipantJpaRepository participantJpaRepository;
    private final PostgresAggregateMapper mapper;

    public PostgresTripRepository(
            TripJpaRepository tripJpaRepository,
            TripParticipantJpaRepository participantJpaRepository,
            PostgresAggregateMapper mapper
    ) {
        this.tripJpaRepository = tripJpaRepository;
        this.participantJpaRepository = participantJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean createTrip(Trip trip) {
        UUID tripId = UUID.fromString(trip.getTripId());
        if (tripJpaRepository.existsById(tripId)) {
            throw new ValidationException("Trip already exists");
        }

        TripEntity tripEntity = new TripEntity();
        tripEntity.setTripId(tripId);
        tripEntity.setTripName(trip.getTripName());
        tripJpaRepository.save(tripEntity);

        List<TripParticipantEntity> participants = trip.getParticipants().stream()
                .map(p -> toParticipantEntity(tripId, p))
                .toList();
        participantJpaRepository.saveAll(participants);
        return true;
    }

    @Override
    public Trip addParticipants(String tripId, List<Participant> participants) {
        UUID tripUuid = parseTripId(tripId);
        ensureTripExists(tripUuid);

        for (Participant participant : participants) {
            participantJpaRepository.findByIdTripIdAndParticipantName(tripUuid, participant.name())
                    .ifPresent(existing -> {
                        throw new ValidationException("Participant already exists. Cannot add duplicate participant: " + existing.getId().getParticipantId());
                    });
            if (participantJpaRepository.existsById(new TripParticipantId(tripUuid, participant.participantId()))) {
                throw new ValidationException("Participant already exists. Cannot add duplicate participant: " + participant.participantId());
            }
        }

        participantJpaRepository.saveAll(participants.stream()
                .map(p -> toParticipantEntity(tripUuid, p))
                .toList());

        return getTripById(tripId);
    }

    @Override
    public Trip removeParticipants(String tripId, List<String> participants) {
        UUID tripUuid = parseTripId(tripId);
        ensureTripExists(tripUuid);

        for (String participantId : participants) {
            TripParticipantEntity entity = participantJpaRepository.findById(new TripParticipantId(tripUuid, participantId))
                    .orElseThrow(() -> new ValidationException("Participant not found in the trip: " + tripId + " and participant id: " + participantId));
            if (!entity.isActive()) {
                throw new ValidationException("Participant not found in the trip: " + tripId + " and participant id: " + participantId);
            }
            entity.setActive(false);
            entity.setRemovedAt(OffsetDateTime.now());
            participantJpaRepository.save(entity);
        }

        return getTripById(tripId);
    }

    @Override
    public void deleteTrip(String tripId) {
        UUID tripUuid = parseTripId(tripId);
        if (!tripJpaRepository.existsById(tripUuid)) {
            throw new ValidationException("You are trying to delete a trip that does not exist");
        }
        tripJpaRepository.deleteById(tripUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getAllTrips() {
        List<TripEntity> trips = tripJpaRepository.findAll();
        if (trips.isEmpty()) {
            throw new ValidationException("No trips found");
        }
        return trips.stream().map(t -> mapper.toTripAggregate(t, true)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Trip getTripById(String tripId) {
        TripEntity tripEntity = tripJpaRepository.findById(parseTripId(tripId))
                .orElseThrow(() -> new ValidationException("Trip not found"));
        return mapper.toTripAggregate(tripEntity, true);
    }

    private void ensureTripExists(UUID tripId) {
        if (!tripJpaRepository.existsById(tripId)) {
            throw new ValidationException("Trip not found");
        }
    }

    private UUID parseTripId(String tripId) {
        return UUID.fromString(tripId);
    }

    private TripParticipantEntity toParticipantEntity(UUID tripId, Participant participant) {
        TripParticipantEntity entity = new TripParticipantEntity();
        entity.setId(new TripParticipantId(tripId, participant.participantId()));
        entity.setParticipantName(participant.name());
        entity.setActive(true);
        entity.setRemovedAt(null);
        return entity;
    }
}
