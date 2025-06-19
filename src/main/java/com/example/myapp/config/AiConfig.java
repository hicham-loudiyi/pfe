package com.example.myapp.config;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AiConfig {

    private final ConfigurationPropertyValue config;

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
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(config.getOllamaBaseUrl())
                .modelName(config.getEmbeddingsModelName())
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public Tokenizer tokenizer() {
        return new HuggingFaceTokenizer();
    }

    @Bean
    public EmbeddingStoreIngestor ingestor(EmbeddingStore<TextSegment> store,
                                           EmbeddingModel model,
                                           Tokenizer tokenizer) {
        return EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .documentSplitter(DocumentSplitters.recursive(
                        config.getDocumentSplitterMaxSegmentSizeInTokens(),
                        config.getDocumentSplitterMaxOverlapSizeInTokens(),
                        tokenizer)
                )
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(config.getContentRetrieverMaxResults())
                .minScore(config.getContentRetrieverMinScore())
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


