package com.jobtrackr.job;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

/** One job application. @Entity maps this class to a database table. */
@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String jobUrl;
    private LocalDate appliedOn;
    private LocalDate followUpOn;

    @Enumerated(EnumType.STRING)   // store "APPLIED", not 0, so reordering the enum is safe
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(length = 5000)
    private String jobDescription;

    @Column(length = 2000)
    private String notes;

    private Instant createdAt = Instant.now();

    protected JobApplication() {}   // JPA needs a no-arg constructor

    public JobApplication(JobApplicationRequest req) {
        apply(req);
    }

    /** Copies editable fields from a request. Used for both create and update. */
    public void apply(JobApplicationRequest req) {
        this.company = req.company();
        this.role = req.role();
        this.jobUrl = req.jobUrl();
        this.appliedOn = req.appliedOn();
        this.followUpOn = req.followUpOn();
        this.jobDescription = req.jobDescription();
        this.notes = req.notes();
    }

    public Long getId() { return id; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public String getJobUrl() { return jobUrl; }
    public LocalDate getAppliedOn() { return appliedOn; }
    public LocalDate getFollowUpOn() { return followUpOn; }
    public ApplicationStatus getStatus() { return status; }
    public String getJobDescription() { return jobDescription; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(ApplicationStatus status) { this.status = status; }
}
