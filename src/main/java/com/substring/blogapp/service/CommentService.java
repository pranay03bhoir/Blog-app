package com.substring.blogapp.service;

import com.substring.blogapp.dto.CommentRequestDto;
import com.substring.blogapp.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto addComment(CommentRequestDto commentRequestDto, String userEmail);

    void deleteComment(Long commentId, String userEmail);

    List<CommentResponseDto> getCommentsForArticle(Long articleId);

    long getCommentCountForArticle(Long articleId);
}
