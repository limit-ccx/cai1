package com.zhiguang.controller;

import com.zhiguang.dto.ApiResponse;
import com.zhiguang.dto.ArticleResponseDto;
import com.zhiguang.entity.User;
import com.zhiguang.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FavoriteController {

    private final ArticleService articleService;

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<ArticleResponseDto>>> getMyFavorites(
            @AuthenticationPrincipal User user) {
        List<ArticleResponseDto> favorites = articleService.getUserFavorites(user.getId());
        return ResponseEntity.ok(ApiResponse.success(favorites));
    }
}
