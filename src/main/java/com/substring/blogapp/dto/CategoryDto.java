package com.substring.blogapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    private String description;

    private String slug;

    private long articleCount;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String description, String slug, long articleCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.articleCount = articleCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String slug;
        private long articleCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder articleCount(long articleCount) { this.articleCount = articleCount; return this; }
        public CategoryDto build() { return new CategoryDto(id, name, description, slug, articleCount); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public long getArticleCount() { return articleCount; }
    public void setArticleCount(long articleCount) { this.articleCount = articleCount; }
}
