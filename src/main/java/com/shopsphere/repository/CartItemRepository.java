package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	
	Optional<CartItem> findByCartAndProduct (Cart cart, Product product);
	List<CartItem> findByCart(Cart cart);
	
	void deleteByProduct(Product product);

}
