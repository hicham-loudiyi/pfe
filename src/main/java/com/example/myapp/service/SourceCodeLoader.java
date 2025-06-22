package com.example.myapp.service;

import com.example.myapp.config.ConfigurationPropertyValue;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceCodeLoader {

    private final EmbeddingStoreIngestor ingestor;
    private final ConfigurationPropertyValue config;

    @PostConstruct
    public void loadSourceCode() {
        Path sourcePath = Paths.get(config.getSourceCodePath());
        if (!Files.exists(sourcePath)) {
            log.warn("Le dossier source {} n'existe pas", sourcePath);
            return;
        }

        try {
            List<Document> documents = new ArrayList<>();
            Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .filter(this::isSourceFile)
                    .forEach(file -> {
                        try {
                            String content = Files.readString(file);
                            String relativePath = sourcePath.relativize(file).toString();

                            // Crée un Document avec le contenu et des métadonnées
                            Document document = Document.from(
                                    content,
                                    Metadata.metadata("file_path", relativePath)
                                            .add("file_name", file.getFileName().toString())
                                            .add("file_extension", getFileExtension(file))
                            );

                            documents.add(document);
                        } catch (IOException e) {
                            log.error("Erreur lors de la lecture du fichier {}", file, e);
                        }
                    });

            if (!documents.isEmpty()) {
                ingestor.ingest(documents);
                log.info("{} fichiers sources chargés dans le RAG", documents.size());
            }
        } catch (IOException e) {
            log.error("Erreur lors du chargement des sources", e);
        }
    }

    private boolean isSourceFile(Path file) {
        String fileName = file.toString().toLowerCase();
        return fileName.endsWith(".java") ||
                fileName.endsWith(".kt") ||
                fileName.endsWith(".py") ||
                fileName.endsWith(".js") ||
                fileName.endsWith(".ts") ||
                fileName.endsWith(".go") ||
                fileName.endsWith(".rs") ||
                fileName.endsWith(".cpp") ||
                fileName.endsWith(".h") ||
                fileName.endsWith(".cs");
    }

    private String getFileExtension(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }
}