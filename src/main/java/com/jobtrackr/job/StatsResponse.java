package com.jobtrackr.job;

import java.util.Map;

/**
 * Pipeline summary. inProgressRate is the percentage of all applications that are
 * CURRENTLY in SCREENING, INTERVIEW or OFFER (the app stores current status only,
 * not history, so someone rejected after an interview counts as REJECTED).
 */
public record StatsResponse(long total, Map<ApplicationStatus, Long> byStatus, double inProgressRate) {
}
