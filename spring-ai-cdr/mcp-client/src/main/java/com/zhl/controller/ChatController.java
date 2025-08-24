package com.zhl.controller;

import com.zhl.bean.ChatEntity;
import com.zhl.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @PostMapping("/doChat")
    public String doChat(@RequestBody ChatEntity chatEntity) {
        chatService.doChat(chatEntity);
        return "ok";
    }
}
