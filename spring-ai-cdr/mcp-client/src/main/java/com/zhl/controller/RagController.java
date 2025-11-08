package com.zhl.controller;

import com.zhl.bean.ChatEntity;
import com.zhl.response.ListResponse;
import com.zhl.service.ChatService;
import com.zhl.service.RagService;
import com.zhl.response.BaseResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Resource
    private RagService ragService;
    @Resource
    private ChatService chatService;

    @PostMapping("/uploadRagDoc")
    public BaseResponse uploadRagDoc(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        org.springframework.core.io.Resource resource = file.getResource();
        ragService.loadText(resource,name);
        return null;
    }

    @GetMapping("/doSearch")
    public ListResponse doSearch(@RequestParam String query){
        List<Document> documents = ragService.doSearch(query);
        return ListResponse.success(documents);
    }
    @PostMapping("/doChatRagSearch")
    public void doChatRagSearch(@RequestBody ChatEntity chatEntity, HttpServletResponse response){
        List<Document> documents = ragService.doSearch(chatEntity.getMessage());
        response.setCharacterEncoding("utf-8");
        ragService.doChatRagSearch(chatEntity, documents);
    }
}
