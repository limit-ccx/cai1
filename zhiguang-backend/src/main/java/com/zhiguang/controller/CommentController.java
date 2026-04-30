package com.zhiguang.controller;

import com.zhiguang.dto.ApiResponse;
import com.zhiguang.dto.CommentRequestDto;
import com.zhiguang.dto.CommentResponseDto;
import com.zhiguang.dto.PageResponse;
import com.zhiguang.entity.User;
import com.zhiguang.service.CommentService;
import com.zhiguang.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponseDto>>> getCommentsByArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<CommentResponseDto> comments = commentService.getCommentsByArticleId(articleId, page, size);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/article/{articleId}/all")
    public ResponseEntity<ApiResponse<java.util.List<CommentResponseDto>>> getAllCommentsByArticle(
            @PathVariable Long articleId) {
        java.util.List<CommentResponseDto> comments = commentService.getArticleComments(articleId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentRequestDto request) {
        if (request.getArticleId() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "文章ID不能为空"));
        }
        CommentResponseDto comment = commentService.createComment(request.getArticleId(), user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("评论发布成功", comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        commentService.deleteComment(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleCommentLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        boolean isLiked = likeService.toggleCommentLike(id, user.getId());
        long likeCount = likeService.getCommentLikeCount(id);
        Map<String, Object> result = Map.of(
                "isLiked", isLiked,
                "likeCount", likeCount
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}