package com.substring.blogapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.substring.blogapp.models.Status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ArticleDto {
    private Long id;
    private String title;
    private String slug;
    private String shortDesc;
    private String content;
    private String coverImageUrl;
    private Integer readingMinutes;
    private Boolean paid;
    private Status status;
    private Double rating;
    private Double price;
    private Long viewsCount;
    private Long likesCount;
    private Long commentsCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long categoryId;

    private CategoryDto category;
    private UserDto user;
    private Set<TagDto> tags = new HashSet<>();

    public ArticleDto() {}

    public ArticleDto(Long id, String title, String slug, String shortDesc, String content, String coverImageUrl, Integer readingMinutes, Boolean paid, Status status, Double rating, Double price, Long viewsCount, Long likesCount, Long commentsCount, LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt, Long categoryId, CategoryDto category, UserDto user, Set<TagDto> tags) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.shortDesc = shortDesc;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.readingMinutes = readingMinutes;
        this.paid = paid;
        this.status = status;
        this.rating = rating;
        this.price = price;
        this.viewsCount = viewsCount;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryId = categoryId;
        this.category = category;
        this.user = user;
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String slug;
        private String shortDesc;
        private String content;
        private String coverImageUrl;
        private Integer readingMinutes;
        private Boolean paid;
        private Status status;
        private Double rating;
        private Double price;
        private Long viewsCount;
        private Long likesCount;
        private Long commentsCount;
        private LocalDateTime publishedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long categoryId;
        private CategoryDto category;
        private UserDto user;
        private Set<TagDto> tags = new HashSet<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder shortDesc(String shortDesc) { this.shortDesc = shortDesc; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder coverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; return this; }
        public Builder readingMinutes(Integer readingMinutes) { this.readingMinutes = readingMinutes; return this; }
        public Builder paid(Boolean paid) { this.paid = paid; return this; }
        public Builder status(Status status) { this.status = status; return this; }
        public Builder rating(Double rating) { this.rating = rating; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder viewsCount(Long viewsCount) { this.viewsCount = viewsCount; return this; }
        public Builder likesCount(Long likesCount) { this.likesCount = likesCount; return this; }
        public Builder commentsCount(Long commentsCount) { this.commentsCount = commentsCount; return this; }
        public Builder publishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder category(CategoryDto category) { this.category = category; return this; }
        public Builder user(UserDto user) { this.user = user; return this; }
        public Builder tags(Set<TagDto> tags) { this.tags = tags; return this; }
        public ArticleDto build() {
            return new ArticleDto(id, title, slug, shortDesc, content, coverImageUrl, readingMinutes, paid, status, rating, price, viewsCount, likesCount, commentsCount, publishedAt, createdAt, updatedAt, categoryId, category, user, tags);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getShortDesc() { return shortDesc; }
    public void setShortDesc(String shortDesc) { this.shortDesc = shortDesc; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Integer getReadingMinutes() { return readingMinutes; }
    public void setReadingMinutes(Integer readingMinutes) { this.readingMinutes = readingMinutes; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getViewsCount() { return viewsCount; }
    public void setViewsCount(Long viewsCount) { this.viewsCount = viewsCount; }

    public Long getLikesCount() { return likesCount; }
    public void setLikesCount(Long likesCount) { this.likesCount = likesCount; }

    public Long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Long commentsCount) { this.commentsCount = commentsCount; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public Set<TagDto> getTags() { return tags; }
    public void setTags(Set<TagDto> tags) { this.tags = tags; }
}
