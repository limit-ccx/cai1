package com.zhiguang.service;

import com.zhiguang.entity.User;
import com.zhiguang.entity.UserFollow;
import com.zhiguang.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserService userService;

    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }

        boolean exists = userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);

        if (exists) {
            userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            return false;
        } else {
            User follower = userService.getUserById(followerId);
            User following = userService.getUserById(followingId);
            UserFollow follow = new UserFollow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            userFollowRepository.save(follow);
            return true;
        }
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public long getFollowerCount(Long userId) {
        return userFollowRepository.countByFollowingId(userId);
    }

    public long getFollowingCount(Long userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    public Map<String, Object> getFollowStatus(Long userId, Long currentUserId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing(currentUserId, userId));
        result.put("followerCount", getFollowerCount(userId));
        result.put("followingCount", getFollowingCount(userId));
        return result;
    }
}
