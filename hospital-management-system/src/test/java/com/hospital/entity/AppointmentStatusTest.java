package com.hospital.entity;

import org.junit.jupiter.api.Test;

import static com.hospital.entity.AppointmentStatus.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentStatusTest {

    @Test
    void validTransitions() {
        assertTrue(PENDING.canMoveTo(CONFIRMED));
        assertTrue(PENDING.canMoveTo(CANCELLED));
        assertTrue(CONFIRMED.canMoveTo(COMPLETED));
        assertTrue(CONFIRMED.canMoveTo(CANCELLED));
    }

    @Test
    void invalidTransitions() {
        assertFalse(PENDING.canMoveTo(COMPLETED));
        assertFalse(COMPLETED.canMoveTo(CANCELLED));
        assertFalse(CANCELLED.canMoveTo(CONFIRMED));
    }
}
