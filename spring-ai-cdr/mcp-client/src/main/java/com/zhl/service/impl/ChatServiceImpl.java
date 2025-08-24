package com.zhl.service.impl;

import com.zhl.bean.ChatEntity;
import com.zhl.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatClient chatClient;

    private String systemPrompt = """
                  你是一个非常聪明的人工助手，可以帮我解决很多问题，你的名字叫小赵
                  """;

    /**
     * 提示词三大类型
     * system
     * user
     * assistant
     *
     */


    public ChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .build();
    }
    @Override
    public String chatTest(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public Flux<ChatResponse> stremResponse(String prompt) {
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    @Override
    public Flux<String> stremResponseStr(String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public Flux<String> doChat(ChatEntity chatEntity) {
        return null;
    }
}
