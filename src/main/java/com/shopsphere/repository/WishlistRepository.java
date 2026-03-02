package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
	
	Optional<Wishlist> findByUserAndProduct(User user, Product product);
	
	List<Wishlist> findByUser(User user);
	
	void deleteByProduct(Product product);

}
