package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.Category;
import com.substring.blogapp.models.Status;
import com.substring.blogapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findByIdAndStatus(Long id, Status status);

    Optional<Article> findBySlugAndStatus(String slug, Status status);

    List<Article> findByCategory(Category category);

    Page<Article> findByCategoryAndStatus(Category category, Status status, Pageable pageable);

    List<Article> findByUser(User user);

    Page<Article> findByUser(User user, Pageable pageable);

    Page<Article> findByUserAndStatus(User user, Status status, Pageable pageable);

    List<Article> findByCategoryAndUser(Category category, User user);

    Page<Article> findByStatus(Status status, Pageable pageable);

    Page<Article> findByTags_SlugAndStatus(String tagSlug, Status status, Pageable pageable);

    @Modifying
    @Query("UPDATE Article a SET a.viewsCount = COALESCE(a.viewsCount, 0) + 1 WHERE a.id = :id")
    void incrementViews(@Param("id") Long id);

    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY (COALESCE(a.viewsCount, 0) * 2 + COALESCE(a.likesCount, 0) * 3) DESC")
    List<Article> findTrendingArticles(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.publishedAt DESC")
    List<Article> findRecentArticles(Pageable pageable);

    long countByStatus(Status status);

    long countByUser(User user);

    long countByCategory(Category category);

    long countByTagsContaining(com.substring.blogapp.models.Tag tag);

    @Query("SELECT COALESCE(SUM(a.viewsCount), 0) FROM Article a")
    long sumTotalViews();

    @Query("SELECT COALESCE(SUM(a.likesCount), 0) FROM Article a")
    long sumTotalLikes();

    @Query("SELECT COALESCE(SUM(a.viewsCount), 0) FROM Article a WHERE a.user = :user")
    long sumViewsByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(a.likesCount), 0) FROM Article a WHERE a.user = :user")
    long sumLikesByUser(@Param("user") User user);
}
