package com.example.myapp.web;

import com.example.myapp.service.ContentExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/errors")
@RequiredArgsConstructor
@Slf4j
public class ErrorAnalysisController {

    private final ContentExtractor extractor;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> analyzeError(
            @RequestPart String stacktrace,
            @RequestPart MultipartFile screenshot) {

        try {
            if (screenshot.isEmpty()) {
                return ResponseEntity.badRequest().body("La capture d'écran est requise.");
            }

            String solution = extractor.analyzeError(stacktrace, screenshot.getBytes());
            return ResponseEntity.ok(solution);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture de l'image", e);
            return ResponseEntity.status(500).body("Erreur de traitement de l'image");
        } catch (Exception e) {
            log.error("Erreur inattendue", e);
            return ResponseEntity.status(500).body("Une erreur s'est produite lors du traitement");
        }
    }
}
