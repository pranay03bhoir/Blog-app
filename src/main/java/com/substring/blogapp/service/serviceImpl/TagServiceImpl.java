package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.TagDto;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.Tag;
import com.substring.blogapp.repositories.ArticleRepository;
import com.substring.blogapp.repositories.TagRepository;
import com.substring.blogapp.service.TagService;
import com.substring.blogapp.utils.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    public TagServiceImpl(TagRepository tagRepository, ArticleRepository articleRepository) {
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getPopularTags() {
        return tagRepository.findPopularTags().stream().limit(10).map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with slug: " + slug));
        return mapToDto(tag);
    }

    @Override
    @Transactional
    public TagDto createTag(String name) {
        String cleanName = name.trim();
        String slug = SlugUtils.toSlug(cleanName);
        Optional<Tag> existing = tagRepository.findBySlug(slug);
        if (existing.isPresent()) {
            return mapToDto(existing.get());
        }
        Tag tag = new Tag(cleanName, slug);
        Tag saved = tagRepository.save(tag);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> result = new HashSet<>();
        if (tagNames == null || tagNames.isEmpty()) {
            return result;
        }

        for (String name : tagNames) {
            if (name == null || name.isBlank()) continue;
            String cleanName = name.trim();
            String slug = SlugUtils.toSlug(cleanName);

            Tag tag = tagRepository.findBySlug(slug)
                    .orElseGet(() -> tagRepository.save(new Tag(cleanName, slug)));
            result.add(tag);
        }
        return result;
    }

    private TagDto mapToDto(Tag tag) {
        long count = articleRepository.countByTagsContaining(tag);
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .articleCount(count)
                .build();
    }
}
