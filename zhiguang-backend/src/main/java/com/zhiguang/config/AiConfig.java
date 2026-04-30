package com.zhiguang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    private String provider = "deepseek";
    private String apiKey;
    private String model = "deepseek-chat";
    private String baseUrl = "https://api.deepseek.com/v1";
    private int maxContentLength = 10000;
    private int timeoutSeconds = 60;
}
