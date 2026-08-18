package com.substring.blogapp.dto;

import jakarta.validation.constraints.NotBlank;

public class TagDto {

    private Long id;

    @NotBlank(message = "Tag name is required")
    private String name;

    private String slug;

    private long articleCount;

    public TagDto() {}

    public TagDto(Long id, String name, String slug, long articleCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.articleCount = articleCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String slug;
        private long articleCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder articleCount(long articleCount) { this.articleCount = articleCount; return this; }
        public TagDto build() { return new TagDto(id, name, slug, articleCount); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public long getArticleCount() { return articleCount; }
    public void setArticleCount(long articleCount) { this.articleCount = articleCount; }
}
