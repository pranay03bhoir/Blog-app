package com.substring.blogapp.service;

import com.substring.blogapp.dto.ArticleDto;
import com.substring.blogapp.dto.ArticleRequestDto;
import com.substring.blogapp.dto.ArticleResponseDto;
import com.substring.blogapp.dto.PageResponse;
import com.substring.blogapp.models.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    // Advanced APIs
    ArticleResponseDto createArticle(ArticleRequestDto requestDto, String userEmail);

    ArticleResponseDto updateArticle(Long id, ArticleRequestDto requestDto, String userEmail);

    void deleteArticle(Long id, String userEmail);

    ArticleResponseDto getArticleById(Long id, String currentUserEmail);

    ArticleResponseDto getArticleBySlug(String slug, String currentUserEmail);

    PageResponse<ArticleResponseDto> searchArticles(
            String keyword,
            Long categoryId,
            String tag,
            Status status,
            Long userId,
            Boolean paid,
            String sortBy,
            String direction,
            int page,
            int size,
            String currentUserEmail
    );

    List<ArticleResponseDto> getTrendingArticles(int limit, String currentUserEmail);

    List<ArticleResponseDto> getRecentArticles(int limit, String currentUserEmail);

    Map<String, Object> toggleLike(Long articleId, String userEmail);

    Map<String, Object> toggleBookmark(Long articleId, String userEmail);

    PageResponse<ArticleResponseDto> getUserBookmarks(String userEmail, int page, int size);

    // Compatibility methods
    List<ArticleDto> getAll();

    Page<ArticleDto> getAllArticlesPaginated(Pageable pageable);

    ArticleDto getArticleById(Long articleId);

    ArticleDto createArticle(ArticleDto articleDto);

    ArticleDto updateArticle(ArticleDto articleDto, Long id);

    void deleteArticle(Long articleId);

    List<ArticleDto> getArticleOfCategory(Long categoryId);

    List<ArticleDto> getArticleOfUser(Long userId);
}
