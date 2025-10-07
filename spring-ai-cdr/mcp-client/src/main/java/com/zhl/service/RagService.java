package com.zhl.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface RagService {

    /**
     * 读取文档并加载到向量库
     * @param resource
     * @param fileName
     */
    public void loadText(Resource resource,String fileName);
    /**
     * 根据提问从知识库中查询相应的知识
     * @param query
     * @return
     */
    public List<Document> doSearch(String query);
}
