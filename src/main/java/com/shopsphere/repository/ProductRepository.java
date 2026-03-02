package com.shopsphere.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {
	
	Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
	
	Page<Product> findByVendor(User vendor, Pageable pageable);

}
