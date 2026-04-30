package com.zhiguang.service;

import com.zhiguang.dto.ArticleRequestDto;
import com.zhiguang.dto.ArticleResponseDto;
import com.zhiguang.dto.PageResponse;
import com.zhiguang.entity.Article;
import com.zhiguang.entity.ArticleFavorite;
import com.zhiguang.entity.User;
import com.zhiguang.exception.ResourceNotFoundException;
import com.zhiguang.repository.ArticleFavoriteRepository;
import com.zhiguang.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;
    private final UserService userService;
    private final LikeService likeService;

    @Transactional
    public ArticleResponseDto createArticle(Long userId, ArticleRequestDto request) {
        User author = userService.getUserById(userId);

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCover(request.getCover());
        article.setTags(request.getTags());
        article.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        article.setAuthor(author);

        article = articleRepository.save(article);
        return toResponseDto(article);
    }

    @Transactional
    public ArticleResponseDto updateArticle(Long articleId, Long userId, ArticleRequestDto request) {
        Article article = getArticleEntityById(articleId);

        if (!article.getAuthor().getId().equals(userId)) {
            throw new ResourceNotFoundException("无权修改此文章");
        }

        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getCover() != null) {
            article.setCover(request.getCover());
        }
        if (request.getTags() != null) {
            article.setTags(request.getTags());
        }
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }

        article = articleRepository.save(article);
        return toResponseDto(article);
    }

    public ArticleResponseDto getArticleById(Long id) {
        return getArticleById(id, null);
    }

    public ArticleResponseDto getArticleById(Long id, Long userId) {
        Article article = getArticleEntityById(id);
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        return toResponseDto(article, userId);
    }

    public Article getArticleEntityById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("文章", "id", id));
    }

    public PageResponse<ArticleResponseDto> getArticles(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Article> articlePage = articleRepository.findByStatus(2, pageable);

        List<ArticleResponseDto> list = articlePage.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                list,
                articlePage.getTotalElements(),
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalPages()
        );
    }

    public PageResponse<ArticleResponseDto> getArticlesByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findByAuthorId(authorId, pageable);

        List<ArticleResponseDto> list = articlePage.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                list,
                articlePage.getTotalElements(),
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalPages()
        );
    }

    public PageResponse<ArticleResponseDto> searchArticles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.searchByKeyword(keyword, pageable);

        List<ArticleResponseDto> list = articlePage.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                list,
                articlePage.getTotalElements(),
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalPages()
        );
    }

    public PageResponse<ArticleResponseDto> getArticlesByTags(List<String> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findByTagsIn(tags, pageable);

        List<ArticleResponseDto> list = articlePage.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                list,
                articlePage.getTotalElements(),
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalPages()
        );
    }

    public List<ArticleResponseDto> getUserFavorites(Long userId) {
        List<ArticleFavorite> favorites = articleFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return favorites.stream()
                .map(fav -> toResponseDto(fav.getArticle(), userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) {
        Article article = getArticleEntityById(articleId);
        if (!article.getAuthor().getId().equals(userId)) {
            throw new ResourceNotFoundException("无权删除此文章");
        }
        articleRepository.delete(article);
    }

    public List<ArticleResponseDto> getHotArticles(int limit) {
        return articleRepository.findTopByViewCount(PageRequest.of(0, limit))
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseDto> getPopularArticles(int limit) {
        return articleRepository.findTopByLikeCount(PageRequest.of(0, limit))
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private ArticleResponseDto toResponseDto(Article article) {
        return toResponseDto(article, null);
    }

    private ArticleResponseDto toResponseDto(Article article, Long userId) {
        ArticleResponseDto.AuthorDto authorDto = new ArticleResponseDto.AuthorDto(
                article.getAuthor().getId(),
                article.getAuthor().getUsername(),
                article.getAuthor().getAvatar(),
                article.getAuthor().getBio()
        );

        boolean isLiked = false;
        boolean isFavorited = false;
        if (userId != null) {
            isLiked = likeService.isLiked(article.getId(), userId);
            isFavorited = likeService.isFavorited(article.getId(), userId);
        }

        return new ArticleResponseDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getSummary(),
                article.getCover(),
                authorDto,
                article.getStatus(),
                article.getViewCount(),
                article.getLikeCount(),
                article.getFavoriteCount(),
                article.getCommentCount(),
                article.getTags(),
                isLiked,
                isFavorited,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}