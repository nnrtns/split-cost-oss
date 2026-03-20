package com.split.trip;

import com.split.trip.accounts.BalanceSheet;
import com.split.trip.accounts.settler.Debt;
import com.split.trip.accounts.settler.Settler;
import com.split.trip.accounts.Transaction;

import java.util.*;

public class Trip {
    String tripId;
    String tripName;
    List<Participant> participants;

    List<Debt> settlements;

    // used to create an access pattern for participants using their name
    private Map<String, Participant> participantMap;

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }

    BalanceSheet balanceSheet;

    public Trip(String tripName, List<Participant> participants) {
        // trip id will be set automatically and should be unique
        // generate a unique trip id using the tripName and participants
        this.tripId = UUID.randomUUID().toString();
        this.tripName = tripName;
        if (participants == null || participants.isEmpty())
            throw new RuntimeException("Participants cannot be empty");
        this.participants = new ArrayList<>(participants);
        this.participantMap = new HashMap<>();
        participants.forEach(participant -> participantMap.put(participant.participantId(), participant));
        this.balanceSheet = new BalanceSheet();
    }

    public Trip addParticipants(List<Participant> participants) {
        List<Participant> duplicate = participants.stream().filter(participant -> participantMap.containsKey(participant.name())).toList();
        if (!duplicate.isEmpty())
            throw new RuntimeException("Duplicate participants found: " + duplicate);
        this.participants.addAll(participants);
        participants.forEach(participant -> participantMap.put(participant.participantId(), participant));
        return this;
    }

    public Trip removeParticipants(List<String> participants) {
        participants.forEach((participant) -> {
            Participant temp = participantMap.get(participant);
            participantMap.remove(participant);
            this.participants.remove(temp);
        });
        return this;
    }

    public Participant getParticipant(String participantId) {
        Participant participant = participantMap.get(participantId);
        if (participant == null)
            throw new RuntimeException("Participant not found in the trip: " + this.tripName + " with id: " + this.tripId + " and participant id: " + participantId);
        return participant;
    }

    public Trip addTransactions(List<Transaction> transactions) {
        balanceSheet.addTransactions(transactions);
        return this;
    }

    public List<Debt> settle(Settler settler) {
        return balanceSheet.settle(settler);

    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Debt> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<Debt> settlements) {
        this.settlements = settlements;
    }

}
