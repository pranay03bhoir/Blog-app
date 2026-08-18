package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.Bookmark;
import com.substring.blogapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);

    Page<Bookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    void deleteByUserAndArticle(User user, Article article);

    long countByUser(User user);
}
