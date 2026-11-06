package com.jobtrackr.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtrackr.common.AiUnavailableException;
import com.jobtrackr.common.BadRequestException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Calls an LLM over plain HTTP so you can see exactly what an AI API call is. */
@Service
public class AiService {

    private final RestClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String model;

    public AiService(ObjectMapper mapper,
                     @Value("${app.ai.api-key}") String apiKey,
                     @Value("${app.ai.model}") String model,
                     @Value("${app.ai.base-url}") String baseUrl) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.model = model;
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public AnalysisResponse analyze(String jobDescription, String resumeText) {
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new BadRequestException("This application has no job description to compare against.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiUnavailableException("Set the ANTHROPIC_API_KEY environment variable to use AI features.");
        }

        String prompt = """
                You are a recruiter assistant. Compare the resume with the job description.
                Respond with ONLY a JSON object, no other text, in exactly this shape:
                {"matchScore": <integer 0-100>, "matchedSkills": [<strings>], "missingSkills": [<strings>], "advice": "<2-3 sentences on how to improve the resume for this role>"}

                JOB DESCRIPTION:
                %s

                RESUME:
                %s
                """.formatted(jobDescription, resumeText);

        String reply = callLlm(prompt);
        try {
            return mapper.readValue(extractJson(reply), AnalysisResponse.class);
        } catch (JsonProcessingException e) {
            // LLMs are not guaranteed to follow the format, so we handle that case.
            throw new AiUnavailableException("The AI reply was not valid JSON. Please try again.");
        }
    }

    private String callLlm(String prompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 800,
                "messages", List.of(Map.of("role", "user", "content", prompt)));
        try {
            JsonNode response = client.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            // The reply looks like: { "content": [ { "type": "text", "text": "..." } ] }
            return response.path("content").path(0).path("text").asText();
        } catch (RestClientException e) {
            throw new AiUnavailableException("AI service call failed: " + e.getMessage());
        }
    }

    /** Models sometimes wrap JSON in ```json fences. Keep only the part from { to }. */
    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        return (start >= 0 && end > start) ? text.substring(start, end + 1) : text;
    }
}
