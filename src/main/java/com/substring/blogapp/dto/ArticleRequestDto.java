package com.substring.blogapp.dto;

import com.substring.blogapp.models.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class ArticleRequestDto {

    @NotBlank(message = "Article title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Short description cannot exceed 1000 characters")
    private String shortDesc;

    @NotBlank(message = "Article content cannot be empty")
    private String content;

    private String coverImageUrl;

    private Integer readingMinutes;

    private Boolean paid;

    private Double price;

    private Status status;

    private Long categoryId;

    private Set<String> tags = new HashSet<>();

    public ArticleRequestDto() {}

    public ArticleRequestDto(String title, String shortDesc, String content, String coverImageUrl, Integer readingMinutes, Boolean paid, Double price, Status status, Long categoryId, Set<String> tags) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.readingMinutes = readingMinutes;
        this.paid = paid;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String shortDesc;
        private String content;
        private String coverImageUrl;
        private Integer readingMinutes;
        private Boolean paid;
        private Double price;
        private Status status;
        private Long categoryId;
        private Set<String> tags = new HashSet<>();

        public Builder title(String title) { this.title = title; return this; }
        public Builder shortDesc(String shortDesc) { this.shortDesc = shortDesc; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder coverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; return this; }
        public Builder readingMinutes(Integer readingMinutes) { this.readingMinutes = readingMinutes; return this; }
        public Builder paid(Boolean paid) { this.paid = paid; return this; }
        public Builder price(Double price) { this.price = price; return this; }
        public Builder status(Status status) { this.status = status; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }
        public ArticleRequestDto build() {
            return new ArticleRequestDto(title, shortDesc, content, coverImageUrl, readingMinutes, paid, price, status, categoryId, tags);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

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

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
