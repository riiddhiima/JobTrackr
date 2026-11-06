package com.jobtrackr.job;

/**
 * The stages of a job application. The enum also owns the rules for which
 * moves are allowed, so the business rule lives in exactly one place.
 */
public enum ApplicationStatus {
    APPLIED, SCREENING, INTERVIEW, OFFER, REJECTED, WITHDRAWN;

    public boolean canMoveTo(ApplicationStatus next) {
        return switch (this) {
            case APPLIED   -> next == SCREENING || next == REJECTED || next == WITHDRAWN;
            case SCREENING -> next == INTERVIEW || next == REJECTED || next == WITHDRAWN;
            case INTERVIEW -> next == OFFER || next == REJECTED || next == WITHDRAWN;
            case OFFER     -> next == WITHDRAWN;
            case REJECTED, WITHDRAWN -> false;   // final states
        };
    }
}
