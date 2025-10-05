package com.zhl.service;

import org.springframework.core.io.Resource;

public interface RagService {

    /**
     * 读取文档并加载到向量库
     * @param resource
     * @param fileName
     */
    public void loadText(Resource resource,String fileName);
}
