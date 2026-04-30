package com.zhiguang.repository;

import com.zhiguang.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleIdAndParentIsNull(Long articleId, Pageable pageable);

    List<Comment> findByArticleIdAndParentIsNull(Long articleId);

    List<Comment> findByParentId(Long parentId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.article.author.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByUserId(@Param("userId") Long userId, Pageable pageable);
}