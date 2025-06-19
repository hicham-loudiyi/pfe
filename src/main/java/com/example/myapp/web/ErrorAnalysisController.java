package com.example.myapp.web;

import com.example.myapp.agents.ErrorAnalysisAgent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import java.time.Instant;
import java.util.Base64;


@RestController
@RequestMapping("/api/errors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Error Analysis", description = "API d'analyse technique d'erreurs")
public class ErrorAnalysisController {

    private final ErrorAnalysisAgent errorAnalysisAgent;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

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
            // Validation de l'image si présente
            String screenshotBase64 = null;
            if (screenshot != null && !screenshot.isEmpty()) {
                validateImageFile(screenshot);
                screenshotBase64 = Base64.getEncoder().encodeToString(screenshot.getBytes());
            }

            // Appel du service d'analyse
            String analysis = errorAnalysisAgent.analyzeErrorWithRag(stacktrace, screenshotBase64);

            return ResponseEntity.ok(new AnalysisResult(analysis));

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

    private void validateImageFile(MultipartFile file) throws FileSizeException, InvalidFileTypeException {
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileSizeException("La taille du fichier dépasse 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException("Seules les images sont acceptées");
        }
    }

    // Exceptions métier
    private static class FileSizeException extends Exception {
        public FileSizeException(String message) { super(message); }
    }

    private static class InvalidFileTypeException extends Exception {
        public InvalidFileTypeException(String message) { super(message); }
    }

    // DTO de réponse
    @Getter
    @AllArgsConstructor
    public static class AnalysisResult {
        @Schema(description = "Résultat de l'analyse technique")
        private final String analysis;

        @Schema(description = "Horodatage de la réponse", example = "2023-10-05T14:30:00Z")
        private final Instant timestamp = Instant.now();
    }
}
