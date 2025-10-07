package com.zhl.controller;

import com.zhl.response.ListResponse;
import com.zhl.service.RagService;
import com.zhl.response.BaseResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Resource
    private RagService ragService;

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

}
