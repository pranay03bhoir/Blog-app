package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.CommentRequestDto;
import com.substring.blogapp.dto.CommentResponseDto;
import com.substring.blogapp.dto.UserDto;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.Article;
import com.substring.blogapp.models.Comment;
import com.substring.blogapp.models.Role;
import com.substring.blogapp.models.User;
import com.substring.blogapp.repositories.ArticleRepository;
import com.substring.blogapp.repositories.CommentRepository;
import com.substring.blogapp.repositories.UserRepository;
import com.substring.blogapp.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(commentRequestDto.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + commentRequestDto.getArticleId()));

        Comment comment = new Comment();
        comment.setContent(commentRequestDto.getContent());
        comment.setUser(user);
        comment.setArticle(article);

        if (commentRequestDto.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(commentRequestDto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + commentRequestDto.getParentCommentId()));
            comment.setParentComment(parent);
        }

        Comment saved = commentRepository.save(comment);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        boolean isCommentAuthor = comment.getUser().getId().equals(user.getId());
        boolean isArticleAuthor = comment.getArticle().getUser() != null && comment.getArticle().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isCommentAuthor && !isArticleAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsForArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        List<Comment> rootComments = commentRepository.findByArticleAndParentCommentIsNullOrderByCreatedAtDesc(article);
        return rootComments.stream().map(this::mapToDtoWithReplies).toList();
    }

    @Override
    public long getCommentCountForArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        return commentRepository.countByArticle(article);
    }

    private CommentResponseDto mapToDto(Comment comment) {
        UserDto userDto = null;
        if (comment.getUser() != null) {
            userDto = UserDto.builder()
                    .id(comment.getUser().getId())
                    .name(comment.getUser().getName())
                    .email(comment.getUser().getEmail())
                    .avatarUrl(comment.getUser().getAvatarUrl())
                    .tagline(comment.getUser().getTagline())
                    .role(comment.getUser().getRole())
                    .build();
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(userDto)
                .articleId(comment.getArticle().getId())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();
    }

    private CommentResponseDto mapToDtoWithReplies(Comment comment) {
        CommentResponseDto dto = mapToDto(comment);
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream().map(this::mapToDtoWithReplies).toList());
        }
        return dto;
    }
}
