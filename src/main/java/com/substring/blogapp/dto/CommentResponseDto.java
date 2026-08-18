package com.substring.blogapp.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponseDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto user;
    private Long articleId;
    private Long parentCommentId;
    private List<CommentResponseDto> replies = new ArrayList<>();

    public CommentResponseDto() {}

    public CommentResponseDto(Long id, String content, LocalDateTime createdAt, LocalDateTime updatedAt, UserDto user, Long articleId, Long parentCommentId, List<CommentResponseDto> replies) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.articleId = articleId;
        this.parentCommentId = parentCommentId;
        this.replies = replies != null ? replies : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserDto user;
        private Long articleId;
        private Long parentCommentId;
        private List<CommentResponseDto> replies = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder user(UserDto user) { this.user = user; return this; }
        public Builder articleId(Long articleId) { this.articleId = articleId; return this; }
        public Builder parentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; return this; }
        public Builder replies(List<CommentResponseDto> replies) { this.replies = replies; return this; }
        public CommentResponseDto build() { return new CommentResponseDto(id, content, createdAt, updatedAt, user, articleId, parentCommentId, replies); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

    public List<CommentResponseDto> getReplies() { return replies; }
    public void setReplies(List<CommentResponseDto> replies) { this.replies = replies; }
}
