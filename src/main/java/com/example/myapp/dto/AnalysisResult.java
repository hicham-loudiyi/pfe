package com.example.myapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AnalysisResult {
    @Schema(description = "Résultat de l'analyse technique")
    private final String analysis;

    @Schema(description = "Horodatage de la réponse", example = "2023-10-05T14:30:00Z")
    private final Instant timestamp = Instant.now();
}
