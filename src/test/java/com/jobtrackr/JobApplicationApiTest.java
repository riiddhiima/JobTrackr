package com.jobtrackr;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

// Blank key on purpose, so tests never call the real AI API even if your machine has a key set.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.ai.api-key=")
class JobApplicationApiTest {

    @Autowired
    MockMvc mvc;

    private static final String NEW_JOB = """
            {"company":"Acme","role":"Junior AI Engineer","appliedOn":"2026-11-06",
             "jobDescription":"Python, machine learning, REST APIs"}
            """;

    private long createJob() throws Exception {
        String body = mvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_JOB))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private void patchStatus(long id, String status, int expectedHttp) throws Exception {
        mvc.perform(patch("/api/applications/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + status + "\"}"))
                .andExpect(status().is(expectedHttp));
    }

    @Test
    void createsApplicationWithAppliedStatus() throws Exception {
        createJob();
    }

    @Test
    void movesToNextAllowedStatus() throws Exception {
        long id = createJob();
        patchStatus(id, "SCREENING", 200);
    }

    @Test
    void blocksInvalidStatusJump() throws Exception {
        long id = createJob();
        patchStatus(id, "OFFER", 409);
    }

    @Test
    void rejectsBlankCompany() throws Exception {
        String bad = "{\"company\":\"\",\"role\":\"Dev\",\"appliedOn\":\"2026-11-06\"}";
        mvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void statsCountsApplications() throws Exception {
        createJob();
        mvc.perform(get("/api/applications/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.byStatus.APPLIED").isNumber());
    }

    @Test
    void analyzeNeedsApiKey() throws Exception {
        long id = createJob();
        mvc.perform(post("/api/applications/" + id + "/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resumeText\":\"Python, Flask, RAG\"}"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void returns404ForMissingApplication() throws Exception {
        mvc.perform(get("/api/applications/9999")).andExpect(status().isNotFound());
    }
}
