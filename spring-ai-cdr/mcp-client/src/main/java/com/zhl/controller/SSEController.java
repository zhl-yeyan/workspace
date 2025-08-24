package com.zhl.controller;

import com.zhl.enums.SSEMsgType;
import com.zhl.service.ChatService;
import com.zhl.utils.SSEServer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping("/sse")
public class SSEController {
    @Resource
    private ChatService chatService;

    @GetMapping(path = "/connect", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@RequestParam String userId) throws Exception {
        return SSEServer.connect(userId);
    }
    /**
     * SSE发送单个消息
     * @param userId
     * @param msg
     * @return
     */
    @GetMapping("/sendMsg")
    public Object sendMsg(@RequestParam String userId,@RequestParam String msg) throws Exception {
        SSEServer.sendMsg(userId,msg, SSEMsgType.MESSAGE);
        return "success";
    }
}
