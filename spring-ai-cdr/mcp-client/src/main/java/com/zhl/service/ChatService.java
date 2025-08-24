package com.zhl.service;

import com.zhl.bean.ChatEntity;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    public String chatTest(String prompt);

    /**
     * 流式聊天
     * @param prompt
     * @return
     */
    public Flux<ChatResponse> stremResponse(String prompt);

    public Flux<String> stremResponseStr(String prompt);

    void doChat(ChatEntity chatEntity);
}
