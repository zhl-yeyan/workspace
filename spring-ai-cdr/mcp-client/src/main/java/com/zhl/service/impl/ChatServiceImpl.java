package com.zhl.service.impl;

import cn.hutool.json.JSONUtil;
import com.zhl.bean.ChatEntity;
import com.zhl.bean.ChatResponseEntity;
import com.zhl.enums.SSEMsgType;
import com.zhl.service.ChatService;
import com.zhl.utils.SSEServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    public void doChat(ChatEntity chatEntity) {
        String userId = chatEntity.getCurrentUserName();
        String prompt = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        List<String> collect = stringFlux.toStream().map(chatResponse -> {
            String s = chatResponse.toString();
            SSEServer.sendMsg(userId, s, SSEMsgType.ADD);
            return s;
        }).collect(Collectors.toList());
        String fullContent = collect.stream().collect(Collectors.joining());
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent,botMsgId);
        log.info("chatResponseEntity:{}",JSONUtil.toJsonStr(chatResponseEntity));
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity),SSEMsgType.FINISH);
    }
}
