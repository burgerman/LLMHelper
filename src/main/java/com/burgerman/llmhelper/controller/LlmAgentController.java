package com.burgerman.llmhelper.controller;

import com.burgerman.llmhelper.service.LlmAgent;
import dev.langchain4j.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ai")
public class LlmAgentController {
    private static final Logger logger = LoggerFactory.getLogger(LlmAgentController.class);

    private final LlmAgent llmAgent;

    public LlmAgentController(LlmAgent llmAgent) {
        this.llmAgent = llmAgent;
    }

    public String getResponse(String userMessage) {
        logger.debug("Sending to Ollama: {}",  userMessage);
        Result<String> res = llmAgent.answer(userMessage);
        String answer = res.content();
        logger.debug("Receiving from Ollama: {}",  answer);
        if (answer != null && !answer.isEmpty()) {
            return answer;
        } else {
            logger.error("Invalid Ollama response for:\n{}", userMessage);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Ollama didn't generate a valid response",
                    null);
        }
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String userMessage) {
        return getResponse(userMessage);
    }

}
