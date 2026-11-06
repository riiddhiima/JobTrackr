package com.jobtrackr.ai;

import jakarta.validation.constraints.NotBlank;

public record AnalysisRequest(@NotBlank String resumeText) {
}
