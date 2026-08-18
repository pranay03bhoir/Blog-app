package com.substring.blogapp.controller;

import com.substring.blogapp.dto.*;
import com.substring.blogapp.models.Status;
import com.substring.blogapp.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "Articles", description = "Endpoints for creating, searching, reading, liking, and bookmarking blog articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponseDto> createArticle(
            @Valid @RequestBody ArticleRequestDto articleRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ArticleResponseDto created = articleService.createArticle(articleRequestDto, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "Update an existing article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponseDto> updateArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody ArticleRequestDto articleRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ArticleResponseDto updated = articleService.updateArticle(articleId, articleRequestDto, email);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "Get article by numeric ID and increment view count")
    public ResponseEntity<ArticleResponseDto> getArticleById(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(articleService.getArticleById(articleId, email));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get article by URL slug and increment view count")
    public ResponseEntity<ArticleResponseDto> getArticleBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(articleService.getArticleBySlug(slug, email));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter articles with multi-criteria parameters")
    public ResponseEntity<PageResponse<ArticleResponseDto>> searchArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        PageResponse<ArticleResponseDto> response = articleService.searchArticles(
                keyword, categoryId, tag, status, userId, paid, sortBy, direction, page, size, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending")
    @Operation(summary = "Get top trending articles by popularity and views")
    public ResponseEntity<List<ArticleResponseDto>> getTrendingArticles(
            @RequestParam(defaultValue = "6") int limit,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(articleService.getTrendingArticles(limit, email));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get most recent published articles")
    public ResponseEntity<List<ArticleResponseDto>> getRecentArticles(
            @RequestParam(defaultValue = "6") int limit,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(articleService.getRecentArticles(limit, email));
    }

    @PostMapping("/{articleId}/like")
    @Operation(summary = "Toggle like/clap for an article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(articleService.toggleLike(articleId, userDetails.getUsername()));
    }

    @PostMapping("/{articleId}/bookmark")
    @Operation(summary = "Toggle bookmark for an article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(articleService.toggleBookmark(articleId, userDetails.getUsername()));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "Get current user's bookmarked articles", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<ArticleResponseDto>> getUserBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(articleService.getUserBookmarks(userDetails.getUsername(), page, size));
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "Delete an article by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        articleService.deleteArticle(articleId, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all articles list (legacy endpoint)")
    public List<ArticleDto> getAll() {
        return articleService.getAll();
    }

    @GetMapping("/all/paginated")
    @Operation(summary = "Get all articles paginated (legacy endpoint)")
    public Page<ArticleDto> getAllArticlePaginated(Pageable pageable) {
        return articleService.getAllArticlesPaginated(pageable);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get articles belonging to a specific category")
    public List<ArticleDto> getArticlesByCategory(@PathVariable Long categoryId) {
        return articleService.getArticleOfCategory(categoryId);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get articles authored by a specific user")
    public List<ArticleDto> getArticlesByUser(@PathVariable Long userId) {
        return articleService.getArticleOfUser(userId);
    }
}
