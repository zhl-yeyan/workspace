package com.zhl.service.impl;

import cn.hutool.json.JSONUtil;
import com.zhl.bean.ChatEntity;
import com.zhl.bean.ChatResponseEntity;
import com.zhl.enums.SSEMsgType;
import com.zhl.service.RagService;
import com.zhl.utils.SSEServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RagServiceImpl implements RagService {
    @Autowired
    private RedisVectorStore  redisVectorStore;

    private ChatClient chatClient;

    private String systemPrompt = """
                  你是一个非常聪明的人工助手，可以帮我解决很多问题，你的名字叫小赵
                  """;
    public RagServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public void loadText(Resource resource, String fileName) {

        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName",fileName);
        List<Document> documents = textReader.get();

        //切分文档，这是默认的文本切分器，也可以自定义文本切分器
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> apply = tokenTextSplitter.apply(documents);

        //向量存储
        redisVectorStore.add(apply);
    }

    @Override
    public List<Document> doSearch(String query) {
        List<Document> documents = redisVectorStore.similaritySearch(query);
        return documents;
    }

    private static final String ragPrompt = """
                  基于上下文的知识库内容回答问题：
                  【上下文】
                  {context}
                  
                  【问题】
                  {question}
                  
                  【输出】
                  如果没有查到，请回复：不知道。
                  如果查到，请回复具体的内容。不相关的近似内容不必提到。
                  """;
    /**
     * rag知识库检索汇总给大模型输出
     * @param chatEntity
     * @param ragContext
     */
    @Override
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        //构建提示词
        String context = null;
        if (ragContext != null && ragContext.size() > 0){
            context = ragContext.stream().
                    map(Document::getText).
                    collect(Collectors.joining("\n"));
        }
        //组装提示词
        Prompt prompt = new Prompt(ragPrompt
                .replace("{context}", context)
                .replace("{question}", question)
        );

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        List<String> collect = stringFlux.toStream().map(chatResponse -> {
            String s = chatResponse.toString();
            SSEServer.sendMsg(userId, s, SSEMsgType.ADD);
            return s;
        }).collect(Collectors.toList());
        String fullContent = collect.stream().collect(Collectors.joining());
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent,botMsgId);
        log.info("chatResponseEntity:{}", JSONUtil.toJsonStr(chatResponseEntity));
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity),SSEMsgType.FINISH);
    }
}
