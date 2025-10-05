package com.zhl.service.impl;

import com.zhl.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class RagServiceImpl implements RagService {
    @Autowired
    private RedisVectorStore  redisVectorStore;



    @Override
    public void loadText(Resource resource, String fileName) {

        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName",fileName);
        List<Document> documents = textReader.get();

        //切分文档
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> apply = tokenTextSplitter.apply(documents);

        //向量存储
        redisVectorStore.add(apply);
    }
}
