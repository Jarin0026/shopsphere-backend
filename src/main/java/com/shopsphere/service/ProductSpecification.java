package com.shopsphere.service;

import org.springframework.data.jpa.domain.Specification;

import com.shopsphere.entity.Product;

public class ProductSpecification {

	public static Specification<Product> hasCategory(Long categoryId) {
		return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);

	}

	public static Specification<Product> nameContains(String search) {
		return (root, query, cb) -> search == null ? null
				: cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
	}
	
	public static Specification<Product> hasVendor(Long vendorId) {

	    return (root, query, cb) -> {
	        if (vendorId == null) {
	            return cb.conjunction(); // no filter
	        }

	        return cb.equal(root.get("vendor").get("id"), vendorId);
	    };
	}
	
	

}
