package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.CategoryDto;
import com.substring.blogapp.exceptions.AlreadyExistsException;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.Category;
import com.substring.blogapp.repositories.ArticleRepository;
import com.substring.blogapp.repositories.CategoryRepository;
import com.substring.blogapp.service.CategoryService;
import com.substring.blogapp.utils.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ArticleRepository articleRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistsException("Category with name '" + categoryDto.getName() + "' already exists.");
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        if (categoryDto.getSlug() != null && !categoryDto.getSlug().isBlank()) {
            category.setSlug(categoryDto.getSlug());
        } else {
            category.setSlug(SlugUtils.toSlug(categoryDto.getName()));
        }
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        if (categoryDto.getSlug() != null && !categoryDto.getSlug().isBlank()) {
            category.setSlug(categoryDto.getSlug());
        } else {
            category.setSlug(SlugUtils.toSlug(categoryDto.getName()));
        }
        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> fetchAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getACategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return mapToDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return mapToDto(category);
    }

    private CategoryDto mapToDto(Category category) {
        long count = articleRepository.countByCategory(category);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .articleCount(count)
                .build();
    }
}
