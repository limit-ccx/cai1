package com.zhiguang.service;

import com.zhiguang.config.AiConfig;
import com.zhiguang.dto.PolishRequestDto;
import com.zhiguang.dto.PolishResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiConfig aiConfig;
    private final WebClient.Builder webClientBuilder;

    /** 各润色模式对应的 system prompt */
    private static final Map<String, String> MODE_PROMPTS = Map.of(
        "polish",
        "你是一位专业的中文写作编辑。请对以下文章内容进行润色优化，提升表达的流畅性、准确性和文学性，但保持原意不变。直接输出润色后的文本，不要添加任何解释说明。",
        "formal",
        "你是一位专业的公文写作编辑。请将以下内容改写为更加正式、规范的书面语风格，保持原意不变。直接输出改写后的文本，不要添加任何解释说明。",
        "casual",
        "你是一位轻松风格的文案编辑。请将以下内容改写为更加轻松、亲切、口语化的风格，保持原意不变。直接输出改写后的文本，不要添加任何解释说明。",
        "concise",
        "你是一位精简写作专家。请将以下内容精简提炼，去除冗余表述，保留核心信息。直接输出精简后的文本，不要添加任何解释说明。",
        "expand",
        "你是一位内容扩写专家。请在保持原意的基础上，对以下内容进行合理扩写，增加细节和论述，使内容更加丰富饱满。直接输出扩写后的文本，不要添加任何解释说明。"
    );

    /**
     * 润色文章内容
     *
     * @param request 润色请求
     * @return 润色结果
     */
    public PolishResponseDto polishContent(PolishRequestDto request) {
        String mode = (request.getMode() != null && !request.getMode().isBlank())
                ? request.getMode() : "polish";
        String systemPrompt = MODE_PROMPTS.getOrDefault(mode, MODE_PROMPTS.get("polish"));

        // 如果提供了标题，在 prompt 中加入上下文
        String userMessage = request.getContent();
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            userMessage = "文章标题：" + request.getTitle() + "\n\n文章正文：\n" + request.getContent();
        }

        // 构建 OpenAI 兼容格式请求体
        Map<String, Object> requestBody = Map.of(
            "model", aiConfig.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user",   "content", userMessage)
            ),
            "temperature", 0.7,
            "max_tokens", 4096
        );

        log.info("调用 AI 润色，模式: {}，模型: {}，内容长度: {}",
                mode, aiConfig.getModel(), request.getContent().length());

        try {
            WebClient webClient = webClientBuilder
                .baseUrl(aiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(aiConfig.getTimeoutSeconds()))
                .block();

            String polishedContent = extractContent(response);
            log.info("AI 润色完成，润色后内容长度: {}", polishedContent.length());

            return new PolishResponseDto(
                request.getContent(),
                polishedContent,
                mode,
                aiConfig.getModel()
            );

        } catch (Exception e) {
            log.error("AI 润色失败", e);
            throw new RuntimeException("AI 润色失败，请稍后重试：" + e.getMessage());
        }
    }

    /**
     * 从 OpenAI 兼容响应中提取 content 文本
     */
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("解析 AI 响应失败，原始响应: {}", response, e);
            throw new RuntimeException("解析 AI 响应失败");
        }
    }
}
