package com.substring.blogapp.service;

import com.substring.blogapp.dto.ArticleRequestDto;
import com.substring.blogapp.dto.ArticleResponseDto;
import com.substring.blogapp.dto.CategoryDto;
import com.substring.blogapp.dto.PageResponse;
import com.substring.blogapp.models.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    private CategoryDto sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = categoryService.createCategory(
                CategoryDto.builder().name("Cloud Architecture " + System.currentTimeMillis()).description("Cloud guides").build());
    }

    @Test
    void testCreateAndFetchArticle() {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title("Testing Cloud Deployments with Kubernetes")
                .content("# Kubernetes Deployment\nDeploying high performance containers.")
                .shortDesc("Quick overview of k8s.")
                .categoryId(sampleCategory.getId())
                .tags(Set.of("Kubernetes", "DevOps"))
                .status(Status.PUBLISHED)
                .build();

        ArticleResponseDto created = articleService.createArticle(requestDto, "admin@blogapp.com");

        assertNotNull(created.getId());
        assertEquals("Testing Cloud Deployments with Kubernetes", created.getTitle());
        assertTrue(created.getSlug().contains("testing-cloud-deployments-with-kubernetes"));
        assertEquals(1, created.getReadingMinutes());
        assertEquals(sampleCategory.getName(), created.getCategory().getName());
        assertEquals(2, created.getTags().size());

        // Fetch by slug
        ArticleResponseDto fetched = articleService.getArticleBySlug(created.getSlug(), "admin@blogapp.com");
        assertEquals(created.getId(), fetched.getId());
        assertTrue(fetched.getViewsCount() >= 1);
    }

    @Test
    void testLikeAndBookmarkArticle() {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title("Spring Boot 4 Observability")
                .content("Detailed guide on metrics, tracing, and logging in Spring Boot.")
                .shortDesc("Spring observability deep-dive.")
                .status(Status.PUBLISHED)
                .build();

        ArticleResponseDto created = articleService.createArticle(requestDto, "alex@blogapp.com");

        // Like article
        Map<String, Object> likeResult = articleService.toggleLike(created.getId(), "sophia@blogapp.com");
        assertTrue((Boolean) likeResult.get("liked"));
        assertEquals(1L, likeResult.get("likesCount"));

        // Bookmark article
        Map<String, Object> bookmarkResult = articleService.toggleBookmark(created.getId(), "sophia@blogapp.com");
        assertTrue((Boolean) bookmarkResult.get("bookmarked"));

        // Check bookmarks list
        PageResponse<ArticleResponseDto> bookmarks = articleService.getUserBookmarks("sophia@blogapp.com", 0, 10);
        assertEquals(1, bookmarks.getTotalElements());
        assertEquals(created.getId(), bookmarks.getContent().get(0).getId());
    }

    @Test
    void testSearchArticles() {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title("Microservices Resilience Patterns")
                .content("Circuit breakers and retry mechanisms in Java.")
                .shortDesc("Resilience design.")
                .status(Status.PUBLISHED)
                .build();

        articleService.createArticle(requestDto, "admin@blogapp.com");

        PageResponse<ArticleResponseDto> searchResults = articleService.searchArticles(
                "Resilience", null, null, Status.PUBLISHED, null, null, "createdAt", "desc", 0, 10, null);

        assertTrue(searchResults.getTotalElements() >= 1);
    }
}
