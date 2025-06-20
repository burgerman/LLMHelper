package com.burgerman.llmhelper.config;

import com.burgerman.llmhelper.controller.LlmAgentController;
import com.burgerman.llmhelper.data.RagBase;
import com.burgerman.llmhelper.service.LlmAgent;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class LlmAgentConfig {
    private static final Logger logger = LoggerFactory.getLogger(LlmAgentConfig.class);

    @Value("${langchain4j.ollama.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.ollama.embedding-model.embedding-model-name}")
    private String embeddingModelName;

    @Value("${langchain4j.ollama.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.ollama.chat-model.timeout}")
    private int chatTimeOut;

    @Bean
    public ChatModel languageModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(chatTimeOut))
                .build();
    }

    @Bean(name = "ollamaChatMemory")
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(2);
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel) {
        return RagBase.ingestData(embeddingModel);
    }

    @Bean(name = "ollamaContentRetriever")
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        // You will need to adjust these parameters to find the optimal setting,
        // which will depend on multiple factors, for example:
        // - The nature of your data
        // - The embedding model you are using
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel) // Use the *same* embedding model used during ingestion
                .maxResults(2) // Retrieve top 2 most relevant segments
                .minScore(0.7) // Filter out segments with relevance score below 0.6
                .build();
    }


    @Bean
    EmbeddingModel embeddingModel(){
        // not good but works for this demo
        logger.debug("Creating embedding model");
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embeddingModelName)
                .build();
    }

    @Bean
    public LlmAgent assistant(ContentRetriever contentRetriever, ChatModel model, ChatMemory memory) {
        logger.debug("Creating LlmAgent");
        return AiServices.builder(LlmAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .chatMemory(memory)
                .build();
    }

}
