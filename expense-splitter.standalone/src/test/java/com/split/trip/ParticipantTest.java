package com.split.trip;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    @Test
    void implicitConstructorShouldGenerateUsableParticipantId() {
        Participant participant = new Participant("Pranav");

        assertEquals("Pranav", participant.name());
        assertNotNull(participant.participantId());
        assertFalse(participant.participantId().isBlank());
    }

    @Test
    void shouldPreserveExplicitParticipantId() {
        Participant participant = new Participant("Pranav", "p-101");

        assertEquals("Pranav", participant.name());
        assertEquals("p-101", participant.participantId());
    }
}
