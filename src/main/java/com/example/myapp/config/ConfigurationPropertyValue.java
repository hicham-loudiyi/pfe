package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConfigurationPropertyValue {

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model-name}")
    private String ollamaModelName;

    @Value("${temperature}")
    private double temperature;

    @Value("${ollama.timeout}")
    private int ollamaTimeout;

    @Value("${ollama.embedding-timeout}")
    private int ollamaEmbeddingTimeout;

    @Value("${chat-memory.max-messages}")
    private int chatMemoryMaxMessages;
}