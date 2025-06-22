package com.example.myapp.web;

import com.example.myapp.dto.AnalysisResult;
import com.example.myapp.exceptions.FileSizeException;
import com.example.myapp.exceptions.InvalidFileTypeException;
import com.example.myapp.service.ErrorAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/errors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Error Analysis", description = "API d'analyse technique d'erreurs")
public class ErrorAnalysisController {

    private final ErrorAnalysisService errorAnalysisService;

    @Operation(summary = "Analyser une erreur technique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyse réussie",
                    content = @Content(schema = @Schema(implementation = AnalysisResult.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "413", description = "Fichier trop volumineux"),
            @ApiResponse(responseCode = "415", description = "Type de fichier non supporté"),
            @ApiResponse(responseCode = "500", description = "Erreur interne")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisResult> analyzeError(
            @RequestPart @NotBlank @Parameter(description = "Stacktrace de l'erreur") String stacktrace,
            @RequestPart(required = false) @Parameter(description = "Capture d'écran optionnelle") MultipartFile screenshot) {

        try {
            AnalysisResult result = errorAnalysisService.analyzeError(stacktrace, screenshot);
            return ResponseEntity.ok(result);
        } catch (FileSizeException e) {
            log.warn("Fichier trop volumineux: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        } catch (InvalidFileTypeException e) {
            log.warn("Type de fichier non supporté: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        } catch (Exception e) {
            log.error("Erreur lors de l'analyse", e);
            return ResponseEntity.internalServerError()
                    .body(new AnalysisResult("Erreur lors du traitement de la requête"));
        }
    }
}
