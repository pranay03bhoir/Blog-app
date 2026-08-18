package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.*;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.*;
import com.substring.blogapp.repositories.*;
import com.substring.blogapp.repositories.specifications.ArticleSpecification;
import com.substring.blogapp.service.ArticleService;
import com.substring.blogapp.service.TagService;
import com.substring.blogapp.utils.ReadingTimeUtils;
import com.substring.blogapp.utils.SlugUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TagService tagService;
    private final ModelMapper modelMapper;

    public ArticleServiceImpl(
            ArticleRepository articleRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            TagRepository tagRepository,
            CommentRepository commentRepository,
            ArticleLikeRepository articleLikeRepository,
            BookmarkRepository bookmarkRepository,
            TagService tagService,
            ModelMapper modelMapper
    ) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.tagService = tagService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ArticleResponseDto createArticle(ArticleRequestDto requestDto, String userEmail) {
        User user = null;
        if (userEmail != null) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        Article article = new Article();
        article.setTitle(requestDto.getTitle());
        article.setShortDesc(requestDto.getShortDesc());
        article.setContent(requestDto.getContent());
        article.setCoverImageUrl(requestDto.getCoverImageUrl());
        article.setPaid(Boolean.TRUE.equals(requestDto.getPaid()));
        article.setPrice(requestDto.getPrice() != null ? requestDto.getPrice() : 0.0);
        article.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : Status.PUBLISHED);

        // Auto calculate reading time
        if (requestDto.getReadingMinutes() != null && requestDto.getReadingMinutes() > 0) {
            article.setReadingMinutes(requestDto.getReadingMinutes());
        } else {
            article.setReadingMinutes(ReadingTimeUtils.calculateReadingMinutes(requestDto.getContent()));
        }

        // Generate unique slug
        String baseSlug = SlugUtils.toSlug(requestDto.getTitle());
        String slug = baseSlug;
        int count = 1;
        while (articleRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }
        article.setSlug(slug);

        // Category
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
            article.setCategory(category);
        }

        // User
        article.setUser(user);

        // Tags
        if (requestDto.getTags() != null && !requestDto.getTags().isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(requestDto.getTags());
            article.setTags(tags);
        }

        Article saved = articleRepository.save(article);
        return mapToResponseDto(saved, userEmail);
    }

    @Override
    @Transactional
    public ArticleResponseDto updateArticle(Long id, ArticleRequestDto requestDto, String userEmail) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        User currentUser = null;
        if (userEmail != null) {
            currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

            boolean isAuthor = article.getUser() != null && article.getUser().getId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

            if (!isAuthor && !isAdmin) {
                throw new AccessDeniedException("You are not authorized to update this article.");
            }
        }

        article.setTitle(requestDto.getTitle());
        article.setShortDesc(requestDto.getShortDesc());
        article.setContent(requestDto.getContent());
        if (requestDto.getCoverImageUrl() != null) {
            article.setCoverImageUrl(requestDto.getCoverImageUrl());
        }
        if (requestDto.getPaid() != null) {
            article.setPaid(requestDto.getPaid());
        }
        if (requestDto.getPrice() != null) {
            article.setPrice(requestDto.getPrice());
        }
        if (requestDto.getStatus() != null) {
            article.setStatus(requestDto.getStatus());
        }

        if (requestDto.getReadingMinutes() != null && requestDto.getReadingMinutes() > 0) {
            article.setReadingMinutes(requestDto.getReadingMinutes());
        } else {
            article.setReadingMinutes(ReadingTimeUtils.calculateReadingMinutes(requestDto.getContent()));
        }

        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
            article.setCategory(category);
        } else {
            article.setCategory(null);
        }

        if (requestDto.getTags() != null) {
            Set<Tag> tags = tagService.getOrCreateTags(requestDto.getTags());
            article.setTags(tags);
        }

        Article saved = articleRepository.save(article);
        return mapToResponseDto(saved, userEmail);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, String userEmail) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (userEmail != null) {
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

            boolean isAuthor = article.getUser() != null && article.getUser().getId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

            if (!isAuthor && !isAdmin) {
                throw new AccessDeniedException("You are not authorized to delete this article.");
            }
        }

        articleRepository.delete(article);
    }

    @Override
    @Transactional
    public ArticleResponseDto getArticleById(Long id, String currentUserEmail) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        articleRepository.incrementViews(id);
        article.setViewsCount((article.getViewsCount() != null ? article.getViewsCount() : 0L) + 1);

        return mapToResponseDto(article, currentUserEmail);
    }

    @Override
    @Transactional
    public ArticleResponseDto getArticleBySlug(String slug, String currentUserEmail) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with slug: " + slug));

        articleRepository.incrementViews(article.getId());
        article.setViewsCount((article.getViewsCount() != null ? article.getViewsCount() : 0L) + 1);

        return mapToResponseDto(article, currentUserEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ArticleResponseDto> searchArticles(
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
    ) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";

        if ("views".equalsIgnoreCase(sortProperty)) sortProperty = "viewsCount";
        if ("likes".equalsIgnoreCase(sortProperty)) sortProperty = "likesCount";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<Article> spec = ArticleSpecification.filterArticles(
                keyword, categoryId, tag, status, userId, paid);

        Page<Article> articlePage = articleRepository.findAll(spec, pageable);

        List<ArticleResponseDto> content = articlePage.getContent().stream()
                .map(a -> mapToResponseDto(a, currentUserEmail))
                .toList();

        return PageResponse.<ArticleResponseDto>builder()
                .content(content)
                .pageNumber(articlePage.getNumber())
                .pageSize(articlePage.getSize())
                .totalElements(articlePage.getTotalElements())
                .totalPages(articlePage.getTotalPages())
                .isLast(articlePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getTrendingArticles(int limit, String currentUserEmail) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        List<Article> articles = articleRepository.findTrendingArticles(pageable);
        return articles.stream().map(a -> mapToResponseDto(a, currentUserEmail)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getRecentArticles(int limit, String currentUserEmail) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        List<Article> articles = articleRepository.findRecentArticles(pageable);
        return articles.stream().map(a -> mapToResponseDto(a, currentUserEmail)).toList();
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long articleId, String userEmail) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Optional<ArticleLike> existingLike = articleLikeRepository.findByUserAndArticle(user, article);
        boolean isLiked;
        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            article.setLikesCount(Math.max(0, (article.getLikesCount() != null ? article.getLikesCount() : 1) - 1));
            isLiked = false;
        } else {
            ArticleLike like = new ArticleLike();
            like.setUser(user);
            like.setArticle(article);
            articleLikeRepository.save(like);
            article.setLikesCount((article.getLikesCount() != null ? article.getLikesCount() : 0) + 1);
            isLiked = true;
        }

        articleRepository.save(article);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likesCount", article.getLikesCount());
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleBookmark(Long articleId, String userEmail) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndArticle(user, article);
        boolean isBookmarked;
        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            isBookmarked = false;
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setArticle(article);
            bookmarkRepository.save(bookmark);
            isBookmarked = true;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bookmarked", isBookmarked);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ArticleResponseDto> getUserBookmarks(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Pageable pageable = PageRequest.of(page, size);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<ArticleResponseDto> content = bookmarkPage.getContent().stream()
                .map(b -> mapToResponseDto(b.getArticle(), userEmail))
                .toList();

        return PageResponse.<ArticleResponseDto>builder()
                .content(content)
                .pageNumber(bookmarkPage.getNumber())
                .pageSize(bookmarkPage.getSize())
                .totalElements(bookmarkPage.getTotalElements())
                .totalPages(bookmarkPage.getTotalPages())
                .isLast(bookmarkPage.isLast())
                .build();
    }

    // Compatibility methods
    @Override
    public List<ArticleDto> getAll() {
        return articleRepository.findAll().stream().map(this::mapToLegacyDto).toList();
    }

    @Override
    public Page<ArticleDto> getAllArticlesPaginated(Pageable pageable) {
        return articleRepository.findAll(pageable).map(this::mapToLegacyDto);
    }

    @Override
    public ArticleDto getArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        return mapToLegacyDto(article);
    }

    @Override
    @Transactional
    public ArticleDto createArticle(ArticleDto articleDto) {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title(articleDto.getTitle())
                .shortDesc(articleDto.getShortDesc())
                .content(articleDto.getContent())
                .coverImageUrl(articleDto.getCoverImageUrl())
                .readingMinutes(articleDto.getReadingMinutes())
                .paid(articleDto.getPaid())
                .price(articleDto.getPrice())
                .status(articleDto.getStatus())
                .categoryId(articleDto.getCategoryId())
                .build();
        ArticleResponseDto created = createArticle(requestDto, null);
        return mapResponseToLegacyDto(created);
    }

    @Override
    @Transactional
    public ArticleDto updateArticle(ArticleDto articleDto, Long id) {
        ArticleRequestDto requestDto = ArticleRequestDto.builder()
                .title(articleDto.getTitle())
                .shortDesc(articleDto.getShortDesc())
                .content(articleDto.getContent())
                .coverImageUrl(articleDto.getCoverImageUrl())
                .readingMinutes(articleDto.getReadingMinutes())
                .paid(articleDto.getPaid())
                .price(articleDto.getPrice())
                .status(articleDto.getStatus())
                .categoryId(articleDto.getCategoryId())
                .build();
        ArticleResponseDto updated = updateArticle(id, requestDto, null);
        return mapResponseToLegacyDto(updated);
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId) {
        deleteArticle(articleId, null);
    }

    @Override
    public List<ArticleDto> getArticleOfCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return articleRepository.findByCategory(category).stream().map(this::mapToLegacyDto).toList();
    }

    @Override
    public List<ArticleDto> getArticleOfUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return articleRepository.findByUser(user).stream().map(this::mapToLegacyDto).toList();
    }

    // Helper mappers
    public ArticleResponseDto mapToResponseDto(Article article, String currentUserEmail) {
        CategoryDto categoryDto = null;
        if (article.getCategory() != null) {
            categoryDto = CategoryDto.builder()
                    .id(article.getCategory().getId())
                    .name(article.getCategory().getName())
                    .description(article.getCategory().getDescription())
                    .slug(article.getCategory().getSlug())
                    .build();
        }

        UserDto userDto = null;
        if (article.getUser() != null) {
            userDto = UserDto.builder()
                    .id(article.getUser().getId())
                    .name(article.getUser().getName())
                    .email(article.getUser().getEmail())
                    .bio(article.getUser().getBio())
                    .avatarUrl(article.getUser().getAvatarUrl())
                    .tagline(article.getUser().getTagline())
                    .role(article.getUser().getRole())
                    .build();
        }

        Set<TagDto> tagDtos = new HashSet<>();
        if (article.getTags() != null) {
            tagDtos = article.getTags().stream()
                    .map(t -> TagDto.builder().id(t.getId()).name(t.getName()).slug(t.getSlug()).build())
                    .collect(Collectors.toSet());
        }

        boolean isLiked = false;
        boolean isBookmarked = false;
        if (currentUserEmail != null) {
            Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
            if (currentUser.isPresent()) {
                isLiked = articleLikeRepository.existsByUserAndArticle(currentUser.get(), article);
                isBookmarked = bookmarkRepository.existsByUserAndArticle(currentUser.get(), article);
            }
        }

        long commentsCount = commentRepository.countByArticle(article);

        return ArticleResponseDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .shortDesc(article.getShortDesc())
                .content(article.getContent())
                .coverImageUrl(article.getCoverImageUrl())
                .readingMinutes(article.getReadingMinutes())
                .paid(article.getPaid())
                .status(article.getStatus())
                .rating(article.getRating())
                .price(article.getPrice())
                .viewsCount(article.getViewsCount() != null ? article.getViewsCount() : 0L)
                .likesCount(article.getLikesCount() != null ? article.getLikesCount() : 0L)
                .commentsCount(commentsCount)
                .isLikedByCurrentUser(isLiked)
                .isBookmarkedByCurrentUser(isBookmarked)
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .category(categoryDto)
                .user(userDto)
                .tags(tagDtos)
                .build();
    }

    private ArticleDto mapToLegacyDto(Article article) {
        ArticleResponseDto resp = mapToResponseDto(article, null);
        return mapResponseToLegacyDto(resp);
    }

    private ArticleDto mapResponseToLegacyDto(ArticleResponseDto resp) {
        return ArticleDto.builder()
                .id(resp.getId())
                .title(resp.getTitle())
                .slug(resp.getSlug())
                .shortDesc(resp.getShortDesc())
                .content(resp.getContent())
                .coverImageUrl(resp.getCoverImageUrl())
                .readingMinutes(resp.getReadingMinutes())
                .paid(resp.getPaid())
                .status(resp.getStatus())
                .rating(resp.getRating())
                .price(resp.getPrice())
                .viewsCount(resp.getViewsCount())
                .likesCount(resp.getLikesCount())
                .commentsCount(resp.getCommentsCount())
                .publishedAt(resp.getPublishedAt())
                .createdAt(resp.getCreatedAt())
                .updatedAt(resp.getUpdatedAt())
                .category(resp.getCategory())
                .user(resp.getUser())
                .tags(resp.getTags())
                .build();
    }
}
