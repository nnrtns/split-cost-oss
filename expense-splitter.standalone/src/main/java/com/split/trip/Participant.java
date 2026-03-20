package com.split.trip;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public record Participant(String name, String participantId) {
    // for simplicity, we will use the same participantId as the name
    public Participant(String name) {
        this(name, generateParticipantId(name));
    }

    private static String generateParticipantId(String name) {
        try {
            String raw = name + "|" + System.currentTimeMillis();
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            String base36 = new BigInteger(1, hash).toString(36).toUpperCase();
            return String.format("%5s", base36).replace(' ', '0').substring(0, 5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return name;
    }
}
