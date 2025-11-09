package com.zhl;

import com.zhl.com.zhl.mcp.tool.DateTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

    /**
     * 注册MCP工具
     * @param dateTool
     * @return
     */
    @Bean
    public ToolCallbackProvider registMCPTools(DateTool dateTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTool).build();
    }
}