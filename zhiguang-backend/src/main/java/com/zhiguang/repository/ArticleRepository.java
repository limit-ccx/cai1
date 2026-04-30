package com.zhiguang.repository;

import com.zhiguang.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByStatus(Integer status, Pageable pageable);

    Page<Article> findByAuthorId(Long authorId, Pageable pageable);

    long countByAuthorIdAndStatus(Long authorId, Integer status);

    @Query("SELECT a FROM Article a WHERE a.status = 2 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%)")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 2 AND a.tags IS NOT EMPTY")
    Page<Article> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 2 ORDER BY a.viewCount DESC")
    List<Article> findTopByViewCount(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 2 ORDER BY a.likeCount DESC")
    List<Article> findTopByLikeCount(Pageable pageable);
}