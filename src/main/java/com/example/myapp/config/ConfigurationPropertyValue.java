package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
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

    @Value("${embeddings-model.model-name}")
    private String embeddingsModelName;

    @Value("${content-retriever.max-results}")
    private int contentRetrieverMaxResults;

    @Value("${content-retriever.min-score}")
    private double contentRetrieverMinScore;

    @Value("${document-splitter.max-segment-size-in-tokens}")
    private int documentSplitterMaxSegmentSizeInTokens;

    @Value("${document-splitter.max-overlap-size-in-tokens}")
    private int documentSplitterMaxOverlapSizeInTokens;
}