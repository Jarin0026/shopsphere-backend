package com.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {
	
	@NotBlank(message = "Category name cannot be empty.")
	private String name;
	
	public CategoryRequest() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
