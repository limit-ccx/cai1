package com.zhiguang.controller;

import com.zhiguang.dto.ApiResponse;
import com.zhiguang.entity.User;
import com.zhiguang.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        boolean isFollowing = followService.toggleFollow(currentUser.getId(), userId);
        long followingCount = followService.getFollowingCount(currentUser.getId());
        long followerCount = followService.getFollowerCount(userId);
        
        Map<String, Object> result = Map.of(
                "isFollowing", isFollowing,
                "followingCount", followingCount,
                "followerCount", followerCount
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{userId}/follow/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFollowStatus(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Map<String, Object> result = followService.getFollowStatus(userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
