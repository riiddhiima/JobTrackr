package com.jobtrackr.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** The structured answer we ask the LLM to produce. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalysisResponse(int matchScore,
                               List<String> matchedSkills,
                               List<String> missingSkills,
                               String advice) {
}
