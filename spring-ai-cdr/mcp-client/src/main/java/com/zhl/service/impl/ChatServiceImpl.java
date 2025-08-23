package com.zhl.service.impl;

import com.zhl.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatClient chatClient;

    private String systemPrompt = """
                  你是一个非常聪明的人工助手，可以帮我解决很多问题，你的名字叫小赵
                  """;

    public ChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .build();
    }
    @Override
    public String chatTest(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }
}
