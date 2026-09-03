package com.college.complaint.service;

import com.college.complaint.dto.CategoryDto;
import com.college.complaint.entity.ComplaintCategory;
import com.college.complaint.repository.ComplaintCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private ComplaintCategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::new)
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category name already exists!");
        }
        ComplaintCategory category = new ComplaintCategory(dto.getName(), dto.getDescription());
        ComplaintCategory saved = categoryRepository.save(category);
        return new CategoryDto(saved);
    }
}
