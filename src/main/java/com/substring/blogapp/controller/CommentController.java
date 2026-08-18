package com.substring.blogapp.controller;

import com.substring.blogapp.dto.CommentRequestDto;
import com.substring.blogapp.dto.CommentResponseDto;
import com.substring.blogapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "Endpoints for article discussion threads and nested replies")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Get comments tree with replies for an article")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.getCommentsForArticle(articleId));
    }

    @PostMapping
    @Operation(summary = "Post a new comment or reply", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentResponseDto> addComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommentResponseDto response = commentService.addComment(commentRequestDto, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        commentService.deleteComment(commentId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
