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
public class ErrorAnalysisService {
    private final ErrorAnalysisAgent errorAnalysisAgent;
    private final FileValidator fileValidator;
    private final VideoProcessor videoProcessor; // Nouveau service à créer

    public AnalysisResult analyzeError(String stacktrace, MultipartFile mediaFile)
            throws FileSizeException, InvalidFileTypeException, IOException {

        String mediaBase64 = null;
        if (mediaFile != null && !mediaFile.isEmpty()) {
            fileValidator.validateMediaFile(mediaFile);
            if (mediaFile.getContentType().startsWith("video/")) {
                mediaBase64 = videoProcessor.extractKeyFrames(mediaFile); // Nouvelle méthode
            } else {
                mediaBase64 = Base64.getEncoder().encodeToString(mediaFile.getBytes());
            }
        }

        String analysis = errorAnalysisAgent.analyzeErrorWithRag(stacktrace, mediaBase64);
        return new AnalysisResult(analysis);
    }
}