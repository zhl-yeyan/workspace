package com.zhl.controller;

import com.zhl.bean.ChatEntity;
import com.zhl.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    @Resource
    private ChatService chatService;

    @PostMapping("/chat/doChat")
    public Flux<String> doChat(@RequestBody ChatEntity chatEntity) {
        return chatService.doChat(chatEntity);
    }


}
