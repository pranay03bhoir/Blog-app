package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.Comment;
import com.substring.blogapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleAndParentCommentIsNullOrderByCreatedAtDesc(Article article);

    List<Comment> findByArticleOrderByCreatedAtAsc(Article article);

    long countByArticle(Article article);

    long countByUser(User user);

    void deleteByArticle(Article article);
}
