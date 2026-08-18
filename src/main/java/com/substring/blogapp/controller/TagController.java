package com.substring.blogapp.controller;

import com.substring.blogapp.dto.TagDto;
import com.substring.blogapp.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@Tag(name = "Tags", description = "Endpoints for article tags and topics")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @Operation(summary = "Get all tags with article counts")
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/popular")
    @Operation(summary = "Get top 10 most popular tags")
    public ResponseEntity<List<TagDto>> getPopularTags() {
        return ResponseEntity.ok(tagService.getPopularTags());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get tag details by slug")
    public ResponseEntity<TagDto> getTagBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(tagService.getTagBySlug(slug));
    }

    @PostMapping
    @Operation(summary = "Create a new tag", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TagDto> createTag(@RequestParam String name) {
        return new ResponseEntity<>(tagService.createTag(name), HttpStatus.CREATED);
    }
}
