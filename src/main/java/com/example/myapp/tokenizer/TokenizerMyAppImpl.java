package com.example.myapp.tokenizer;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.Tokenizer;
import org.springframework.stereotype.Component;


@Component
public class TokenizerMyAppImpl implements Tokenizer {
    private final HuggingFaceTokenizer tokenizer;

    public TokenizerMyAppImpl() {
        this.tokenizer = HuggingFaceTokenizer.newInstance("bert-base-uncased");
    }

    @Override
    public int estimateTokenCountInText(String text) {
        if (text == null || text.isBlank()) return 0;
        return tokenizer.encode(text).getTokens().length;
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage message) {
        if (message == null || message.text() == null) return 0;
        return estimateTokenCountInText(message.text());
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> iterable) {
        return 0;
    }

    @Override
    public int estimateTokenCountInTools(Object objectWithTools) {
        if (objectWithTools == null) return 0;
        return estimateTokenCountInText(objectWithTools.toString());
    }

    @Override
    public int estimateTokenCountInTools(Iterable<Object> objectsWithTools) {
        int total = 0;
        if (objectsWithTools != null) {
            for (Object obj : objectsWithTools) {
                total += estimateTokenCountInTools(obj);
            }
        }
        return total;
    }

    @Override
    public int estimateTokenCountInToolSpecifications(Iterable<ToolSpecification> toolSpecifications) {
        int total = 0;
        if (toolSpecifications != null) {
            for (ToolSpecification spec : toolSpecifications) {
                total += estimateTokenCountInText(spec.toString());
            }
        }
        return total;
    }

    @Override
    public int estimateTokenCountInToolExecutionRequests(Iterable<ToolExecutionRequest> toolExecutionRequests) {
        int total = 0;
        if (toolExecutionRequests != null) {
            for (ToolExecutionRequest req : toolExecutionRequests) {
                total += estimateTokenCountInText(req.toString());
            }
        }
        return total;
    }

    @Override
    public int estimateTokenCountInForcefulToolSpecification(ToolSpecification toolSpecification) {
        if (toolSpecification == null) return 0;
        return estimateTokenCountInText(toolSpecification.toString());
    }

    @Override
    public int estimateTokenCountInForcefulToolExecutionRequest(ToolExecutionRequest toolExecutionRequest) {
        if (toolExecutionRequest == null) return 0;
        return estimateTokenCountInText(toolExecutionRequest.toString());
    }
}