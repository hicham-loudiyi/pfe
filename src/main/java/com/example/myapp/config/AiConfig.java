package com.example.myapp.config;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import org.springframework.core.io.Resource;

@Configuration
public class AiConfig {

    private final ConfigurationPropertieValue configuration;

    public AiConfig(ConfigurationPropertieValue configuration) {
        this.configuration = configuration;
    }

    @Bean
    public ChatLanguageModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl(configuration.getClmBaseUrl())
                .modelName(configuration.getClmModelName())
                .timeout(Duration.ofMinutes(configuration.getTimeout()))
                .temperature(configuration.getTemperature())
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return chatId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(configuration.getEmBaseUrl())
                .modelName(configuration.getEmModelName())
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment>  embeddingStore(EmbeddingModel embeddingModel) {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    ApplicationRunner loadDocumentToVectorStore(
            //ChatLanguageModel chatLanguageModel,
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore,
            Tokenizer tokenizer,
            @Value("classpath:/docs/texte.txt") Resource textResource,
            @Value("classpath:/docs") Resource folderResource,
            @Value("classpath:/docs/document.pdf") Resource pdfResource
            ) {
        return args -> {
            //var doc = FileSystemDocumentLoader.loadDocument(pdfResource.getFile().toPath());
            var ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(1000,100,tokenizer))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
            DocumentParser parser = new ApachePdfBoxDocumentParser();
            Document document = parser.parse(pdfResource.getInputStream());
            ingestor.ingest(document);
        };
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(configuration.getCrMaxResults())
                .minScore(configuration.getCrMinScore())
                .build();
    }

}
