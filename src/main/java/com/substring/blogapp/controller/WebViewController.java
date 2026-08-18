package com.substring.blogapp.controller;

import com.substring.blogapp.dto.*;
import com.substring.blogapp.models.Status;
import com.substring.blogapp.service.ArticleService;
import com.substring.blogapp.service.CategoryService;
import com.substring.blogapp.service.StatsService;
import com.substring.blogapp.service.TagService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebViewController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final StatsService statsService;

    public WebViewController(ArticleService articleService, CategoryService categoryService, TagService tagService, StatsService statsService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;

        PageResponse<ArticleResponseDto> articles = articleService.searchArticles(
                keyword, categoryId, tag, Status.PUBLISHED, null, null, "createdAt", "desc", page, size, email);

        List<ArticleResponseDto> trending = articleService.getTrendingArticles(4, email);
        List<CategoryDto> categories = categoryService.fetchAllCategories();
        List<TagDto> popularTags = tagService.getPopularTags();

        model.addAttribute("articles", articles);
        model.addAttribute("trending", trending);
        model.addAttribute("categories", categories);
        model.addAttribute("popularTags", popularTags);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedTag", tag);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentUser", email);

        return "index";
    }

    @GetMapping("/articles/{slugOrId}")
    public String articleDetail(
            @PathVariable String slugOrId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ArticleResponseDto article;

        try {
            Long id = Long.parseLong(slugOrId);
            article = articleService.getArticleById(id, email);
        } catch (NumberFormatException e) {
            article = articleService.getArticleBySlug(slugOrId, email);
        }

        List<CategoryDto> categories = categoryService.fetchAllCategories();
        List<ArticleResponseDto> related = articleService.getTrendingArticles(3, email);

        model.addAttribute("article", article);
        model.addAttribute("categories", categories);
        model.addAttribute("relatedArticles", related);
        model.addAttribute("currentUser", email);

        return "article";
    }

    @GetMapping("/category/{slugOrId}")
    public String categoryFeed(
            @PathVariable String slugOrId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        CategoryDto category;
        try {
            Long id = Long.parseLong(slugOrId);
            category = categoryService.getACategoryById(id);
        } catch (NumberFormatException e) {
            category = categoryService.getCategoryBySlug(slugOrId);
        }

        PageResponse<ArticleResponseDto> articles = articleService.searchArticles(
                null, category.getId(), null, Status.PUBLISHED, null, null, "createdAt", "desc", page, size, email);

        model.addAttribute("category", category);
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categoryService.fetchAllCategories());
        model.addAttribute("currentUser", email);

        return "category";
    }

    @GetMapping("/tag/{slug}")
    public String tagFeed(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        TagDto tag = tagService.getTagBySlug(slug);

        PageResponse<ArticleResponseDto> articles = articleService.searchArticles(
                null, null, slug, Status.PUBLISHED, null, null, "createdAt", "desc", page, size, email);

        model.addAttribute("tag", tag);
        model.addAttribute("articles", articles);
        model.addAttribute("popularTags", tagService.getPopularTags());
        model.addAttribute("currentUser", email);

        return "tag";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;

        PageResponse<ArticleResponseDto> articles = articleService.searchArticles(
                q, categoryId, tag, Status.PUBLISHED, null, null, "createdAt", "desc", page, size, email);

        model.addAttribute("query", q);
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categoryService.fetchAllCategories());
        model.addAttribute("popularTags", tagService.getPopularTags());
        model.addAttribute("currentUser", email);

        return "search";
    }

    @GetMapping("/editor")
    public String newArticleEditor(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        model.addAttribute("categories", categoryService.fetchAllCategories());
        model.addAttribute("article", new ArticleResponseDto());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentUser", email);
        return "editor";
    }

    @GetMapping("/editor/{id}")
    public String editArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ArticleResponseDto article = articleService.getArticleById(id, email);
        model.addAttribute("categories", categoryService.fetchAllCategories());
        model.addAttribute("article", article);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentUser", email);
        return "editor";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        if (email == null) {
            return "redirect:/login";
        }

        PageResponse<ArticleResponseDto> myArticles = articleService.searchArticles(
                null, null, null, null, null, null, "createdAt", "desc", page, size, email);

        DashboardStatsDto stats = statsService.getUserStats(email);
        DashboardStatsDto globalStats = statsService.getGlobalStats();

        model.addAttribute("articles", myArticles);
        model.addAttribute("stats", stats);
        model.addAttribute("globalStats", globalStats);
        model.addAttribute("currentUser", email);

        return "dashboard";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        if (email == null) {
            return "redirect:/login";
        }

        PageResponse<ArticleResponseDto> bookmarks = articleService.getUserBookmarks(email, page, size);
        model.addAttribute("bookmarks", bookmarks);
        model.addAttribute("currentUser", email);

        return "bookmarks";
    }

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }
}
