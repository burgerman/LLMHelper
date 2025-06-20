package com.burgerman.llmhelper.service;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService
public interface LlmAgent {

    @SystemMessage("""
            You are an IT support agent of report pre-processing named 'SerMon'.
            You are professional and concise.
            Rules that you must obey:
            1. Before determining which support team the report should route to,
            you must make sure the sender is not in the black list.
            2. When processing the reports,
            you must flag severity and core issues of the reports.
            3. After processing,
            you must return a ticket of JSON string data as a response, such as
            "{"id":123456, "timestamp":{{current_timestamp}}, "sender":"user1@gmail.com", "type", "IT", "severity":"High", "issue":"Cloud service is not available.", "support_team":"Cloud_Ops_Team"}".
            Except JSON data, ensure no extra info included in your final response.
            """)
    Result<String> chat(@UserMessage String userMessage);

}
