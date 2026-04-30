package com.zhiguang.service;

import com.zhiguang.dto.CommentRequestDto;
import com.zhiguang.dto.CommentResponseDto;
import com.zhiguang.dto.PageResponse;
import com.zhiguang.entity.Article;
import com.zhiguang.entity.Comment;
import com.zhiguang.entity.User;
import com.zhiguang.exception.ResourceNotFoundException;
import com.zhiguang.repository.ArticleRepository;
import com.zhiguang.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final UserService userService;
    private final LikeService likeService;

    @Transactional
    public CommentResponseDto createComment(Long articleId, Long userId, CommentRequestDto request) {
        Article article = articleService.getArticleEntityById(articleId);
        User user = userService.getUserById(userId);

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setArticle(article);
        comment.setUser(user);

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("评论", "id", request.getParentId()));
            comment.setParent(parent);
        }

        comment = commentRepository.save(comment);

        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);

        return toResponseDto(comment);
    }

    public PageResponse<CommentResponseDto> getCommentsByArticleId(Long articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByArticleIdAndParentIsNull(articleId, pageable);

        List<CommentResponseDto> list = commentPage.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                list,
                commentPage.getTotalElements(),
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalPages()
        );
    }

    public List<CommentResponseDto> getArticleComments(Long articleId) {
        return commentRepository.findByArticleIdAndParentIsNull(articleId)
                .stream()
                .map(c -> toResponseDto(c, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("无权删除此评论");
        }

        Article article = comment.getArticle();
        article.setCommentCount(Math.max(0, article.getCommentCount() - 1));

        commentRepository.delete(comment);
    }

    private CommentResponseDto toResponseDto(Comment comment) {
        return toResponseDto(comment, false);
    }

    private CommentResponseDto toResponseDto(Comment comment, boolean includeReplies) {
        CommentResponseDto.ArticleBriefDto articleDto = null;
        if (comment.getArticle() != null) {
            articleDto = new CommentResponseDto.ArticleBriefDto(
                    comment.getArticle().getId(),
                    comment.getArticle().getTitle()
            );
        }

        CommentResponseDto.UserBriefDto userDto = new CommentResponseDto.UserBriefDto(
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar()
        );

        CommentResponseDto.UserBriefDto parentDto = null;
        if (comment.getParent() != null) {
            parentDto = new CommentResponseDto.UserBriefDto(
                    comment.getParent().getUser().getId(),
                    comment.getParent().getUser().getUsername(),
                    comment.getParent().getUser().getAvatar()
            );
        }

        List<CommentResponseDto> replies = null;
        if (includeReplies) {
            replies = commentRepository.findByParentId(comment.getId())
                    .stream()
                    .map(c -> toResponseDto(c, false))
                    .collect(Collectors.toList());
        }

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                articleDto,
                userDto,
                parentDto,
                comment.getCreatedAt(),
                false,
                0L,
                replies
        );
    }
}