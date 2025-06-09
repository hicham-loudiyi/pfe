package com.example.myapp.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface MyAiAgent {
    @SystemMessage("You are a useful agent, answer the user's question according to the present pdf file.")
    String ask(String question);
}