package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c LEFT JOIN c.articles a GROUP BY c.id ORDER BY COUNT(a) DESC")
    List<Category> findCategoriesOrderByArticleCountDesc();
}
