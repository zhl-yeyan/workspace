package com.zhl.controller;

import com.zhl.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
