package com.zhl.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

public class CustomTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of();
    }
}
