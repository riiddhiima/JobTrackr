package com.jobtrackr.job;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Runs every day at 9:00 and logs which applications need a follow-up. */
@Component
public class FollowUpScheduler {

    private static final Logger log = LoggerFactory.getLogger(FollowUpScheduler.class);

    private final JobApplicationService service;

    public FollowUpScheduler(JobApplicationService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 9 * * *")   // second minute hour day month weekday
    public void logDueFollowUps() {
        List<JobApplication> due = service.followUpsDue();
        if (!due.isEmpty()) {
            log.info("{} application(s) need a follow-up today: {}", due.size(),
                    due.stream().map(a -> a.getCompany() + " (" + a.getRole() + ")").toList());
        }
    }
}
