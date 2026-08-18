package com.substring.blogapp.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    private String avatarUrl;

    @Size(max = 150, message = "Tagline cannot exceed 150 characters")
    private String tagline;

    private String currentPassword;

    private String newPassword;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String name, String bio, String avatarUrl, String tagline, String currentPassword, String newPassword) {
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.tagline = tagline;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
