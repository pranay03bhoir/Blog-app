package com.substring.blogapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.substring.blogapp.models.Role;

import java.time.LocalDateTime;

public class UserDto {

    private Long id;
    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Role role;
    private Boolean enabled;
    private String bio;
    private String avatarUrl;
    private String tagline;
    private LocalDateTime createdAt;
    private long articlesCount;

    public UserDto() {}

    public UserDto(Long id, String name, String email, String password, Role role, Boolean enabled, String bio, String avatarUrl, String tagline, LocalDateTime createdAt, long articlesCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.tagline = tagline;
        this.createdAt = createdAt;
        this.articlesCount = articlesCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private Role role;
        private Boolean enabled;
        private String bio;
        private String avatarUrl;
        private String tagline;
        private LocalDateTime createdAt;
        private long articlesCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder enabled(Boolean enabled) { this.enabled = enabled; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public Builder tagline(String tagline) { this.tagline = tagline; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder articlesCount(long articlesCount) { this.articlesCount = articlesCount; return this; }
        public UserDto build() { return new UserDto(id, name, email, password, role, enabled, bio, avatarUrl, tagline, createdAt, articlesCount); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public long getArticlesCount() { return articlesCount; }
    public void setArticlesCount(long articlesCount) { this.articlesCount = articlesCount; }
}
