package com.zhl.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();
    public static SseEmitter connect(String userId) throws Exception{
        //设置超时时间，0L表示不超时（永不过期)；默认是30秒，超时未完成任务则会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);
        //注册回调方法
        sseEmitter.onTimeout(timeOutCallback(userId));
        sseEmitter.onCompletion(completionCallback(userId));
        sseEmitter.onError(errorCallback(userId));
        sseClients.put(userId,sseEmitter);
        return sseEmitter;
    }

    private static Consumer<Throwable> errorCallback(String userId) {
        return Throwable -> {
            log.error("用户{}连接异常",userId,Throwable);
            removeClient(userId);
        };
    }

    private static Runnable completionCallback(String userId) {
        return () -> {
            log.info("用户{}连接完成",userId);
            removeClient(userId);
        };
    }

    public static Runnable timeOutCallback(String userId) {
        return () -> {
            log.info("用户{}已超时",userId);
            removeClient(userId);
        };
    }
    public static void removeClient(String userId) {
        //删除用户
        sseClients.remove(userId);
        log.info("用户{}已断开连接",userId);
    }
}
