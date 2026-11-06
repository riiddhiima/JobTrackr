package com.jobtrackr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // turns on @Scheduled methods (the daily follow-up check)
public class JobTrackrApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobTrackrApplication.class, args);
    }
}
