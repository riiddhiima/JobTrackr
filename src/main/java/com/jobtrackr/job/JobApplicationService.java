package com.jobtrackr.job;

import com.jobtrackr.common.InvalidTransitionException;
import com.jobtrackr.common.NotFoundException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Business logic lives here, not in the controller. */
@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    // Constructor injection: Spring passes the repository in for us.
    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication create(JobApplicationRequest req) {
        return repository.save(new JobApplication(req));
    }

    public JobApplication get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application " + id + " not found"));
    }

    public List<JobApplication> search(ApplicationStatus status, String company) {
        return repository.search(status, company);
    }

    public JobApplication update(Long id, JobApplicationRequest req) {
        JobApplication app = get(id);
        app.apply(req);
        return repository.save(app);
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    /** Moves an application to a new stage, but only if the workflow allows it. */
    public JobApplication changeStatus(Long id, ApplicationStatus next) {
        JobApplication app = get(id);
        if (!app.getStatus().canMoveTo(next)) {
            throw new InvalidTransitionException(
                    "Cannot move from " + app.getStatus() + " to " + next);
        }
        app.setStatus(next);
        return repository.save(app);
    }

    /** Applications still waiting on a reply whose follow-up date has arrived. */
    public List<JobApplication> followUpsDue() {
        return repository.findByStatusInAndFollowUpOnLessThanEqual(
                List.of(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING), LocalDate.now());
    }

    public StatsResponse stats() {
        List<JobApplication> all = repository.findAll();

        Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus s : ApplicationStatus.values()) {
            byStatus.put(s, 0L);                       // show zero counts too
        }
        all.forEach(a -> byStatus.merge(a.getStatus(), 1L, Long::sum));

        long inProgress = byStatus.get(ApplicationStatus.SCREENING)
                + byStatus.get(ApplicationStatus.INTERVIEW)
                + byStatus.get(ApplicationStatus.OFFER);
        double rate = all.isEmpty() ? 0.0 : Math.round(inProgress * 1000.0 / all.size()) / 10.0;

        return new StatsResponse(all.size(), byStatus, rate);
    }
}
