package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConfigurationPropertyValue {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model-name:${ollama.model-name}}")
    private String ollamaModelName;

    @Value("${temperature:0.1}")
    private double temperature;

    @Value("${ollama.timeout:120}")
    private int ollamaTimeout;

    @Value("${ollama.embedding-timeout:5}")
    private int ollamaEmbeddingTimeout;

    @Value("${chat-memory.max-messages:20}")
    private int chatMemoryMaxMessages;

    @Value("${embeddings-model.model-name:nomic-embed-text}")
    private String embeddingsModelName;

    @Value("${content-retriever.max-results:2}")
    private int contentRetrieverMaxResults;

    @Value("${content-retriever.min-score:0.6}")
    private double contentRetrieverMinScore;

    @Value("${document-splitter.max-segment-size-in-tokens:1000}")
    private int documentSplitterMaxSegmentSizeInTokens;

    @Value("${document-splitter.max-overlap-size-in-tokens:100}")
    private int documentSplitterMaxOverlapSizeInTokens;

    @Value("${source-code.path}")
    private String sourceCodePath;

}