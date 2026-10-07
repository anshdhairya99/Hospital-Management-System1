package com.hospital.entity;

public enum AppointmentStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED;

    /** Allowed lifecycle: PENDING -> CONFIRMED -> COMPLETED, and cancel from PENDING/CONFIRMED. */
    public boolean canMoveTo(AppointmentStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == COMPLETED || next == CANCELLED;
            default -> false;
        };
    }
}
