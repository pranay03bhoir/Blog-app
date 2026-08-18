package com.substring.blogapp.dto;

public class DashboardStatsDto {

    private long totalArticles;
    private long totalPublished;
    private long totalDrafts;
    private long totalViews;
    private long totalLikes;
    private long totalComments;
    private long totalCategories;
    private long totalUsers;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalArticles, long totalPublished, long totalDrafts, long totalViews, long totalLikes, long totalComments, long totalCategories, long totalUsers) {
        this.totalArticles = totalArticles;
        this.totalPublished = totalPublished;
        this.totalDrafts = totalDrafts;
        this.totalViews = totalViews;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
        this.totalCategories = totalCategories;
        this.totalUsers = totalUsers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalArticles;
        private long totalPublished;
        private long totalDrafts;
        private long totalViews;
        private long totalLikes;
        private long totalComments;
        private long totalCategories;
        private long totalUsers;

        public Builder totalArticles(long totalArticles) { this.totalArticles = totalArticles; return this; }
        public Builder totalPublished(long totalPublished) { this.totalPublished = totalPublished; return this; }
        public Builder totalDrafts(long totalDrafts) { this.totalDrafts = totalDrafts; return this; }
        public Builder totalViews(long totalViews) { this.totalViews = totalViews; return this; }
        public Builder totalLikes(long totalLikes) { this.totalLikes = totalLikes; return this; }
        public Builder totalComments(long totalComments) { this.totalComments = totalComments; return this; }
        public Builder totalCategories(long totalCategories) { this.totalCategories = totalCategories; return this; }
        public Builder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public DashboardStatsDto build() {
            return new DashboardStatsDto(totalArticles, totalPublished, totalDrafts, totalViews, totalLikes, totalComments, totalCategories, totalUsers);
        }
    }

    public long getTotalArticles() { return totalArticles; }
    public void setTotalArticles(long totalArticles) { this.totalArticles = totalArticles; }

    public long getTotalPublished() { return totalPublished; }
    public void setTotalPublished(long totalPublished) { this.totalPublished = totalPublished; }

    public long getTotalDrafts() { return totalDrafts; }
    public void setTotalDrafts(long totalDrafts) { this.totalDrafts = totalDrafts; }

    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }

    public long getTotalLikes() { return totalLikes; }
    public void setTotalLikes(long totalLikes) { this.totalLikes = totalLikes; }

    public long getTotalComments() { return totalComments; }
    public void setTotalComments(long totalComments) { this.totalComments = totalComments; }

    public long getTotalCategories() { return totalCategories; }
    public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
}
