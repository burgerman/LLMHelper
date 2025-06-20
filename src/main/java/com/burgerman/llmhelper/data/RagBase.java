package com.burgerman.llmhelper.data;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class RagBase {
    private static final Logger logger = LoggerFactory.getLogger(RagBase.class);

    public static EmbeddingStore<TextSegment> ingestData(EmbeddingModel embeddingModel) {

        logger.debug("Starting knowledge base ingestion...");
        // --- 1. Load knowledge ---
        String resName = "knowledge.txt";
        DocumentParser parser = new TextDocumentParser();
        Document knowledgeDoc = null;
        try (InputStream inputStream = RagBase.class.getClassLoader().getResourceAsStream(resName)) {
            Objects.requireNonNull(inputStream, "Resource not found: " + resName);
            knowledgeDoc = parser.parse(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resName, e);
        }
        List<Document> documents = List.of(knowledgeDoc);
        logger.debug("Documents loaded successfully.");
        // --- 2. Setup Embedding Model Ollama (Ensure Ollama running locally) ---
        logger.debug("Initializing Ollama Embedding Model...");
//        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
//                .baseUrl(DEFAULT_OLLAMA_URL) // Default Ollama URL
//                .modelName(DEFAULT_EMBEDDING_MODEL)
//                .build();
        logger.debug("Embedding Model initialized.");
        // --- 3. Setup Embedding Store ---
        // We use a simple in-memory store for this demo.
        // For persistent storage, explore options like Chroma, Pinecone, Weaviate, etc.
        logger.debug("Initializing In-Memory Embedding Store...");
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        logger.debug("Embedding Store initialized.");
        // --- 4. Setup Ingestion Pipeline ---
        // (split by chunk)
        // recursive(maxSegmentSize, maxOverlap) splits text recursively, keeping paragraphs/sentences together.
        // 400 characters per segment, 40 characters overlap between segments.
        DocumentSplitter splitter = DocumentSplitters.recursive(400, 40);
        logger.debug("Using recursive document splitter (400 chars, 40 overlap).");
        // EmbeddingStoreIngestor handles splitting, embedding, and storing.
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        // --- 5. Ingest Documents ---
        logger.debug("Ingesting documents into the embedding store...");
        ingestor.ingest(documents);
        logger.debug("Ingestion complete. Embedding store contains");
        return embeddingStore;
    }
}
