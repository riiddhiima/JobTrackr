package com.jobtrackr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jobtrackr.job.ApplicationStatus;
import org.junit.jupiter.api.Test;

/** A plain unit test: no Spring, no database, runs in milliseconds. */
class ApplicationStatusTest {

    @Test
    void allowsNormalForwardMoves() {
        assertTrue(ApplicationStatus.APPLIED.canMoveTo(ApplicationStatus.SCREENING));
        assertTrue(ApplicationStatus.SCREENING.canMoveTo(ApplicationStatus.INTERVIEW));
        assertTrue(ApplicationStatus.INTERVIEW.canMoveTo(ApplicationStatus.OFFER));
    }

    @Test
    void blocksSkippingStages() {
        assertFalse(ApplicationStatus.APPLIED.canMoveTo(ApplicationStatus.OFFER));
    }

    @Test
    void finalStatesCannotMove() {
        assertFalse(ApplicationStatus.REJECTED.canMoveTo(ApplicationStatus.APPLIED));
        assertFalse(ApplicationStatus.WITHDRAWN.canMoveTo(ApplicationStatus.INTERVIEW));
    }
}
