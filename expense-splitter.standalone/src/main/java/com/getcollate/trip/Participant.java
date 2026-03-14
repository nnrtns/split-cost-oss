package com.getcollate.trip;

public record Participant(String name, String participantId) {
    // for simplicity, we will use the same participantId as the name
    public Participant(String name) {
        this(name, name);
    }
    public String toString() {
        return name;
    }
}
