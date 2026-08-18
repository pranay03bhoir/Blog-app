package com.substring.blogapp.service;

import com.substring.blogapp.dto.TagDto;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<TagDto> getAllTags();

    List<TagDto> getPopularTags();

    TagDto getTagBySlug(String slug);

    TagDto createTag(String name);

    Set<com.substring.blogapp.models.Tag> getOrCreateTags(Set<String> tagNames);
}
