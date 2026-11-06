package com.jobtrackr.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Incoming JSON for create/update. A record is an immutable data carrier. */
public record JobApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        String jobUrl,
        @NotNull LocalDate appliedOn,
        LocalDate followUpOn,
        String jobDescription,
        String notes) {
}
