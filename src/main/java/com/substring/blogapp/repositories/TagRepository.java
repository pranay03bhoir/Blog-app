package com.substring.blogapp.repositories;

import com.substring.blogapp.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);

    boolean existsByName(String name);

    Set<Tag> findByNameIn(Collection<String> names);

    @Query("SELECT t FROM Tag t LEFT JOIN t.articles a GROUP BY t.id ORDER BY COUNT(a) DESC")
    List<Tag> findPopularTags();
}
