package com.zhiguang.controller;

import com.zhiguang.config.AiConfig;
import com.zhiguang.dto.ApiResponse;
import com.zhiguang.dto.PolishRequestDto;
import com.zhiguang.dto.PolishResponseDto;
import com.zhiguang.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final AiConfig aiConfig;

    /**
     * AI 润色文章接口
     * POST /api/ai/polish
     *
     * 需要登录（由 Spring Security 配置保护）
     */
    @PostMapping("/polish")
    public ResponseEntity<ApiResponse<PolishResponseDto>> polish(
            @Valid @RequestBody PolishRequestDto request) {

        // 校验内容长度
        if (request.getContent().length() > aiConfig.getMaxContentLength()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("内容超过最大长度限制（" + aiConfig.getMaxContentLength() + " 字符）"));
        }

        try {
            PolishResponseDto result = aiService.polishContent(request);
            return ResponseEntity.ok(ApiResponse.success("润色成功", result));
        } catch (RuntimeException e) {
            log.error("AI 润色接口异常", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
}
