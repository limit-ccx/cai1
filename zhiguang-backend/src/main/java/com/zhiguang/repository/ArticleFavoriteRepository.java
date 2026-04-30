package com.zhiguang.repository;

import com.zhiguang.entity.ArticleFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleFavoriteRepository extends JpaRepository<ArticleFavorite, Long> {

    boolean existsByArticleIdAndUserId(Long articleId, Long userId);

    Optional<ArticleFavorite> findByArticleIdAndUserId(Long articleId, Long userId);

    void deleteByArticleIdAndUserId(Long articleId, Long userId);

    long countByArticleId(Long articleId);

    List<ArticleFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);
}
