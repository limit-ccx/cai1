package com.zhiguang.repository;

import com.zhiguang.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    boolean existsByArticleIdAndUserId(Long articleId, Long userId);

    Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);

    void deleteByArticleIdAndUserId(Long articleId, Long userId);

    long countByArticleId(Long articleId);
}
