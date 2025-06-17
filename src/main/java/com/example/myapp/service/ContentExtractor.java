package com.example.myapp.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContentExtractor {

    private final ChatLanguageModel llm;

    public String analyzeError(String stacktrace, byte[] screenshot) {
        String response = generateResponseFromImageAndText(stacktrace, screenshot);
        log.debug("Réponse du LLM : {}", response);
        return response;
    }

    /**
     * Méthode générique pour interagir avec le LLM multimodal.
     */
    @Cacheable("llmResponses")
    public String generateResponseFromImageAndText(String textInput, byte[] imageBytes) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String prompt = """
                [img]data:image/jpeg;base64,%s
                %s
                """.formatted(base64Image, textInput);

            log.debug("Envoi au LLM : {}", prompt);
            return llm.generate(prompt);

        } catch (Exception e) {
            log.error("Échec de l'analyse multimodale", e);
            throw new RuntimeException("Erreur lors de l'appel au LLM multimodal", e);
        }
    }
}
