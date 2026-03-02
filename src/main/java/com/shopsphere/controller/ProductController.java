package com.shopsphere.controller;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping(consumes = "multipart/form-data")
	@PreAuthorize("hasRole('VENDOR')")
	public ProductResponse createProduct(
	        @RequestParam String name,
	        @RequestParam String description,
	        @RequestParam BigDecimal price,
	        @RequestParam int stock,
	        @RequestParam Long categoryId,
	        @RequestParam List<MultipartFile> images

) {

	    return productService.createProduct(
	            name, description, price, stock, categoryId, images);
	}


	@GetMapping
	public Page<ProductResponse> getProducts(@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "price") String sortBy,
			@RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		return productService.getAllProducts(categoryId, search, sortBy, direction, page, size);
	}
	
	@PutMapping(value = "/{id}", consumes = "multipart/form-data")
	@PreAuthorize("hasRole('VENDOR')")
	public ProductResponse updateProduct(
	        @PathVariable Long id,
	        @RequestParam String name,
	        @RequestParam String description,
	        @RequestParam BigDecimal price,
	        @RequestParam int stock,
	        @RequestParam Long categoryId,
	        @RequestParam(required = false) List<MultipartFile> images) {

	    return productService.updateProduct(
	            id, name, description, price, stock, categoryId, images);
	}

	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('VENDOR')")
	public String deleteProduct(@PathVariable Long id) {
		return productService.deleteProduct(id);
	}
	
	@GetMapping("/{id}")
	public ProductResponse getProductById(@PathVariable Long id) {
	    return productService.getProductById(id);
	}

	
	
}
