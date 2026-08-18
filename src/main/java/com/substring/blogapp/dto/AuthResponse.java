package com.substring.blogapp.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private UserDto user;
    private long expiresInMs;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, UserDto user, long expiresInMs) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
        this.expiresInMs = expiresInMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String tokenType = "Bearer";
        private UserDto user;
        private long expiresInMs;

        public Builder token(String token) { this.token = token; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder user(UserDto user) { this.user = user; return this; }
        public Builder expiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; return this; }
        public AuthResponse build() { return new AuthResponse(token, tokenType, user, expiresInMs); }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }
}
