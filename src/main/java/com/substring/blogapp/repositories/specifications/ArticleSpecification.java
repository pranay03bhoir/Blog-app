package com.substring.blogapp.repositories.specifications;

import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.Status;
import com.substring.blogapp.models.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ArticleSpecification {

    public static Specification<Article> filterArticles(
            String keyword,
            Long categoryId,
            String tag,
            Status status,
            Long userId,
            Boolean paid
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("shortDesc")), pattern);
                Predicate contentMatch = criteriaBuilder.like(root.get("content"), "%" + keyword.trim() + "%");
                predicates.add(criteriaBuilder.or(titleMatch, descMatch, contentMatch));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (tag != null && !tag.trim().isEmpty()) {
                Join<Article, Tag> tagJoin = root.join("tags", JoinType.INNER);
                Predicate tagSlugMatch = criteriaBuilder.equal(criteriaBuilder.lower(tagJoin.get("slug")), tag.trim().toLowerCase());
                Predicate tagNameMatch = criteriaBuilder.equal(criteriaBuilder.lower(tagJoin.get("name")), tag.trim().toLowerCase());
                predicates.add(criteriaBuilder.or(tagSlugMatch, tagNameMatch));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (paid != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
