package com.example.myapp.service;

import com.example.myapp.agents.ErrorAnalysisAgent;
import com.example.myapp.dto.AnalysisResult;
import com.example.myapp.exceptions.FileSizeException;
import com.example.myapp.exceptions.InvalidFileTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorAnalysisService {

    private final ErrorAnalysisAgent errorAnalysisAgent;
    private final FileValidator fileValidator;

    public AnalysisResult analyzeError(String stacktrace, MultipartFile screenshot)
            throws FileSizeException, InvalidFileTypeException, IOException {

        // Validation de l'image si présente
        String screenshotBase64 = null;
        if (screenshot != null && !screenshot.isEmpty()) {
            fileValidator.validateImageFile(screenshot);
            screenshotBase64 = Base64.getEncoder().encodeToString(screenshot.getBytes());
        }

        // Appel du service d'analyse
        String analysis = errorAnalysisAgent.analyzeErrorWithRag(stacktrace, screenshotBase64);

        return new AnalysisResult(analysis);
    }
}