package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConfigurationPropertyValue {
    @Value("${chat-model.base-url}")
    private String chatModelBaseUrl;
    
    @Value("${chat-model.model-name}")
    private String chatModelModelName;
    
    @Value("${timeout}")
    private int timeout;
    
    @Value("${temperature}")
    private double temperature;
    
    @Value("${embeddings-model.base-url}")
    private String embeddingsModelBaseUrl;
    
    @Value("${embeddings-model.model-name}")
    private String embeddingsModelModelName;
    
    @Value("${content-retriever.max-results}")
    private int contentRetrieverMaxResults;
    
    @Value("${content-retriever.min-score}")
    private double contentRetrieverMinScore;

    @Value("${chat-memory.max-messages}")
    private int chatMemoryMaxMessages;

    @Value("${document-splitter.max-segment-size-in-tokens}")
    private int documentSplitterMaxSegmentSizeInTokens;

    @Value("${document-splitter.max-overlap-size-in-tokens}")
    private int documentSplitterMaxOverlapSizeInTokens;

}