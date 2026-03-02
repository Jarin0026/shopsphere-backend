package com.shopsphere.service;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.repository.CategoryRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public String createCategory(CategoryRequest request) {

		if (categoryRepository.findByName(request.getName()).isPresent()) {
			throw new RuntimeException("Category already exists");
		}

		Category category = new Category(request.getName());
		categoryRepository.save(category);

		return "Category created successfully";
	}

	public List<CategoryResponse> getAllCategories() {

	    return categoryRepository.findAll()
	            .stream()
	            .map(c -> new CategoryResponse(
	                    c.getId(),
	                    c.getName()))
	            .toList();
	}


}
