package com.jobtrackr.job;

import com.jobtrackr.ai.AiService;
import com.jobtrackr.ai.AnalysisRequest;
import com.jobtrackr.ai.AnalysisResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** HTTP layer: maps URLs to methods and converts JSON to objects and back. */
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;
    private final AiService aiService;

    public JobApplicationController(JobApplicationService service, AiService aiService) {
        this.service = service;
        this.aiService = aiService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication create(@Valid @RequestBody JobApplicationRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<JobApplication> search(@RequestParam(required = false) ApplicationStatus status,
                                       @RequestParam(required = false) String company) {
        return service.search(status, company);
    }

    @GetMapping("/{id}")
    public JobApplication get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @Valid @RequestBody JobApplicationRequest req) {
        return service.update(id, req);
    }

    // PATCH = change one thing (the status), unlike PUT which replaces the editable fields.
    @PatchMapping("/{id}/status")
    public JobApplication changeStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        return service.changeStatus(id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return service.stats();
    }

    @GetMapping("/follow-ups-due")
    public List<JobApplication> followUpsDue() {
        return service.followUpsDue();
    }

    /** Compares your resume text with this application's job description using an LLM. */
    @PostMapping("/{id}/analyze")
    public AnalysisResponse analyze(@PathVariable Long id, @Valid @RequestBody AnalysisRequest req) {
        JobApplication app = service.get(id);
        return aiService.analyze(app.getJobDescription(), req.resumeText());
    }
}
