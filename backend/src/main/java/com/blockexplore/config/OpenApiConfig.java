package com.blockexplore.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api-v1")
                .pathsToMatch("/**") // 定义你希望生成文档的 API 路径
                .build();
    }
}
