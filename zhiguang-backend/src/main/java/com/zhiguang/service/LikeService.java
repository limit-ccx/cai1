package com.zhiguang.service;

import com.zhiguang.entity.Article;
import com.zhiguang.entity.ArticleFavorite;
import com.zhiguang.entity.ArticleLike;
import com.zhiguang.entity.User;
import com.zhiguang.repository.ArticleFavoriteRepository;
import com.zhiguang.repository.ArticleLikeRepository;
import com.zhiguang.repository.ArticleRepository;
import com.zhiguang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        boolean exists = articleLikeRepository.existsByArticleIdAndUserId(articleId, userId);

        if (exists) {
            articleLikeRepository.deleteByArticleIdAndUserId(articleId, userId);
            article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
            articleRepository.save(article);
            log.info("取消点赞: articleId={}, userId={}", articleId, userId);
            return false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            ArticleLike like = new ArticleLike();
            like.setArticle(article);
            like.setUser(user);
            articleLikeRepository.save(like);
            article.setLikeCount(article.getLikeCount() + 1);
            articleRepository.save(article);
            log.info("点赞成功: articleId={}, userId={}", articleId, userId);
            return true;
        }
    }

    public boolean isLiked(Long articleId, Long userId) {
        if (userId == null) {
            return false;
        }
        return articleLikeRepository.existsByArticleIdAndUserId(articleId, userId);
    }

    public long getLikeCount(Long articleId) {
        return articleRepository.findById(articleId)
                .map(Article::getLikeCount)
                .orElse(0);
    }

    @Transactional
    public boolean toggleFavorite(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        boolean exists = articleFavoriteRepository.existsByArticleIdAndUserId(articleId, userId);

        if (exists) {
            articleFavoriteRepository.deleteByArticleIdAndUserId(articleId, userId);
            article.setFavoriteCount(Math.max(0, article.getFavoriteCount() - 1));
            articleRepository.save(article);
            log.info("取消收藏: articleId={}, userId={}", articleId, userId);
            return false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            ArticleFavorite favorite = new ArticleFavorite();
            favorite.setArticle(article);
            favorite.setUser(user);
            articleFavoriteRepository.save(favorite);
            article.setFavoriteCount(article.getFavoriteCount() + 1);
            articleRepository.save(article);
            log.info("收藏成功: articleId={}, userId={}", articleId, userId);
            return true;
        }
    }

    public boolean isFavorited(Long articleId, Long userId) {
        if (userId == null) {
            return false;
        }
        return articleFavoriteRepository.existsByArticleIdAndUserId(articleId, userId);
    }

    public long getFavoriteCount(Long articleId) {
        return articleRepository.findById(articleId)
                .map(Article::getFavoriteCount)
                .orElse(0);
    }

    public Map<String, Object> getLikeStatus(Long articleId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked(articleId, userId));
        result.put("likeCount", getLikeCount(articleId));
        result.put("isFavorited", isFavorited(articleId, userId));
        result.put("favoriteCount", getFavoriteCount(articleId));
        return result;
    }

    public boolean toggleCommentLike(Long commentId, Long userId) {
        return false;
    }

    public boolean isCommentLiked(Long commentId, Long userId) {
        return false;
    }

    public long getCommentLikeCount(Long commentId) {
        return 0;
    }
}
