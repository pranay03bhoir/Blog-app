package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.ArticleLike;
import com.substring.blogapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    Optional<ArticleLike> findByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);

    long countByArticle(Article article);

    void deleteByUserAndArticle(User user, Article article);
}
