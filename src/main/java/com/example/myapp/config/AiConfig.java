package com.example.myapp.config;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AiConfig {

    private final ConfigurationPropertyValue config;

    public AiConfig(ConfigurationPropertyValue config) {
        this.config = config;
    }

    @Bean
    public ChatLanguageModel llm() {
        return OllamaChatModel.builder()
                .baseUrl(config.getOllamaBaseUrl())
                .modelName(config.getOllamaModelName())
                .temperature(config.getTemperature())
                .timeout(Duration.ofMinutes(config.getOllamaTimeout()))
                .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(config.getChatMemoryMaxMessages())
                .build();
    }

    @Bean
    ApplicationRunner llmHealthCheck(ChatLanguageModel llm) {
        return args -> {
            try {
                String testResponse = llm.generate("Réponds 'OK' si tu es opérationnel.");
                if (!testResponse.contains("OK")) {
                    log.warn("⚠️ Le LLM a répondu mais sans 'OK'. Réponse : {}", testResponse);
                } else {
                    log.info("✅ Connexion au LLM réussie.");
                }
            } catch (Exception e) {
                log.error("❌ Échec de l'initialisation du LLM : {}", e.getMessage());
            }
        };
    }
}