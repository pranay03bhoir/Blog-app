package com.substring.blogapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestDto {

    @NotNull(message = "Article ID is required")
    private Long articleId;

    @NotBlank(message = "Comment content cannot be blank")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String content;

    private Long parentCommentId;

    public CommentRequestDto() {}

    public CommentRequestDto(Long articleId, String content, Long parentCommentId) {
        this.articleId = articleId;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long articleId;
        private String content;
        private Long parentCommentId;

        public Builder articleId(Long articleId) { this.articleId = articleId; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder parentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; return this; }
        public CommentRequestDto build() { return new CommentRequestDto(articleId, content, parentCommentId); }
    }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }
}
