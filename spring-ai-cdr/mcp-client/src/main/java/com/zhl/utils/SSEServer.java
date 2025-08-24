package com.zhl.utils;

import com.zhl.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        log.info("用户{}已连接",userId);
        // 连接后立即发送一次握手消息，确保浏览器 onopen/onmessage 可见
        sendSseEmitterMsg(sseEmitter, userId, "connected", SSEMsgType.MESSAGE);
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


    public static void sendSseEmitterMsg(SseEmitter sseEmitter, String userId, String msg, SSEMsgType type) {

        try {
            SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                    .id(userId)
                    .data(msg)
                    .name(type.type);
            if (sseEmitter != null) {
                sseEmitter.send(msgEvent);
            }
        } catch (Exception e) {
            log.error("用户{}发送消息异常",userId,e);
            removeClient(userId);
        }
    }

    public static void sendMsg(String userId, String msg, SSEMsgType type) {
        //判断sseClients是否为空
        if (sseClients.isEmpty()) {
            return;
        }
        //判断userId是否存在
        if (!sseClients.containsKey(userId)) {
            return;
        }

        // 修复bug：正确使用ifPresentOrElse方法
        Optional.ofNullable(sseClients.get(userId)).ifPresentOrElse(
                sseEmitter -> sendSseEmitterMsg(sseEmitter, userId, msg, type),
                () -> removeClient(userId)
        );
    }

    public static void sendMsgAll(String msg) {
        //判断sseClients是否为空
        if (sseClients.isEmpty()) {
            return;
        }
        // 创建需要移除的客户端列表
        List<String> clientsToRemove = new ArrayList<>();
        // 遍历所有客户端并发送消息
        for (Map.Entry<String, SseEmitter> entry : sseClients.entrySet()) {
            String userId = entry.getKey();
            SseEmitter sseEmitter = entry.getValue();

            if (sseEmitter != null) {
                sendSseEmitterMsg(sseEmitter, userId, msg, SSEMsgType.MESSAGE);
            } else {
                clientsToRemove.add(userId);
            }
        }
        // 移除无效的客户端连接
        clientsToRemove.forEach(SSEServer::removeClient);
    }
}
