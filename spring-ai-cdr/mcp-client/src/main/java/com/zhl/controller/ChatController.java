package com.zhl.controller;

import com.zhl.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.http.HttpResponse;

@RestController
public class HelloController {
    @Resource
    private ChatService chatService;
    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/chatTest")
    public String chatTest(String msg) {
        return chatService.chatTest(msg);
    }
    @GetMapping("/strem/response")
    public Flux<ChatResponse> stremResponse(String msg) {
        return chatService.stremResponse(msg);
    }
    @GetMapping("/strem/str")
    public Flux<String> stremResponseStr(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.stremResponseStr(msg);
    }

}
