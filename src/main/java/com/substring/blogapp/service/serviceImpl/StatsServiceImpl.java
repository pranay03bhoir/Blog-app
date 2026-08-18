package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.DashboardStatsDto;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.Status;
import com.substring.blogapp.models.User;
import com.substring.blogapp.repositories.*;
import com.substring.blogapp.service.StatsService;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public StatsServiceImpl(ArticleRepository articleRepository, UserRepository userRepository, CategoryRepository categoryRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public DashboardStatsDto getGlobalStats() {
        return DashboardStatsDto.builder()
                .totalArticles(articleRepository.count())
                .totalPublished(articleRepository.countByStatus(Status.PUBLISHED))
                .totalDrafts(articleRepository.countByStatus(Status.DRAFT))
                .totalViews(articleRepository.sumTotalViews())
                .totalLikes(articleRepository.sumTotalLikes())
                .totalComments(commentRepository.count())
                .totalCategories(categoryRepository.count())
                .totalUsers(userRepository.count())
                .build();
    }

    @Override
    public DashboardStatsDto getUserStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return DashboardStatsDto.builder()
                .totalArticles(articleRepository.countByUser(user))
                .totalViews(articleRepository.sumViewsByUser(user))
                .totalLikes(articleRepository.sumLikesByUser(user))
                .totalComments(commentRepository.countByUser(user))
                .build();
    }
}
