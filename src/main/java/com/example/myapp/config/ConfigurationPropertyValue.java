package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConfigurationPropertyValue {

    @Value("${max.mediasize:20 971 520}")
    private long maxMediaSize;

    @Value("${ollama.baseurl:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.modelname:qwen2.5vl:7b}")
    private String ollamaModelName;

    @Value("${ollama.temperature:0.1}")
    private double temperature;

    @Value("${ollama.timeout:5}")
    private int ollamaTimeout;

    @Value("${ollama.chatmemory.maxmessages:20}")
    private int chatMemoryMaxMessages;

    @Value("${ollama.embeddings.modelname:nomic-embed-text}")
    private String embeddingsModelName;

    @Value("${ollama.embeddings.timeout:5}")
    private int ollamaEmbeddingTimeout;

    @Value("${contentretriever.maxresults:2}")
    private int contentRetrieverMaxResults;

    @Value("${contentretriever.minscore:0.6}")
    private double contentRetrieverMinScore;

    @Value("${documentsplitter.maxsegmentsizeintokens:1000}")
    private int documentSplitterMaxSegmentSizeInTokens;

    @Value("${documentsplitter.maxoverlapsizeintokens:100}")
    private int documentSplitterMaxOverlapSizeInTokens;

    @Value("${sourcecode.path}")
    private String sourceCodePath;

}