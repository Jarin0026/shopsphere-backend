package com.shopsphere.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.CartItemResponse;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final CartItemRepository cartItemRepository;

	public CartService(CartRepository cartRepository, ProductRepository productRepository,
			UserRepository userRepository, CartItemRepository cartItemRepository) {
		super();
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.cartItemRepository = cartItemRepository;
	}

	public String addToCart(Long productId, int quantity) {
		
		 if (quantity <= 0) {
	            throw new RuntimeException("Quantity must be greater than zero");
	        }
		
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

		if (!user.getRole().name().equals("CUSTOMER")) {
			throw new RuntimeException("Only customer can add to cart.");
		}

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found."));

		if (product.getStock() < quantity) {
			throw new RuntimeException("Insufficient stock");
		}

		Cart cart = cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(new Cart(user)));

		Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			int newQuantity = item.getQuantity() + quantity;

			if (product.getStock() < newQuantity) {
				throw new RuntimeException("Insufficient stock");
			}
			item.setQuantity(newQuantity);
			cartItemRepository.save(item);
		} else {

			CartItem item = new CartItem(cart, product, quantity);

			cartItemRepository.save(item);
		}

		return "Cart updated successfully.";
	}
	
	public CartResponse viewCart() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found."));
		
		Cart cart = cartRepository.findByUser(user).orElseThrow(()-> new RuntimeException("Cart is empty."));
		
		List<CartItem> items = cartItemRepository.findByCart(cart);
		
		CartResponse response = new CartResponse();
		List<CartItemResponse> itemResponses = new ArrayList<>();
		
		BigDecimal total = BigDecimal.ZERO;
		
		for(CartItem item : items) {
			CartItemResponse itemDto = new CartItemResponse();
			
			itemDto.setCartItemId(item.getId());
			itemDto.setProductId(item.getProduct().getId());
			itemDto.setProductName(item.getProduct().getName());
			itemDto.setQuantity(item.getQuantity());
			itemDto.setPrice(item.getProduct().getPrice());
			
			BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			
			itemDto.setSubtotal(subtotal);
			
			total = total.add(subtotal);
			
			itemResponses.add(itemDto);
			
		}
		
		response.setItems(itemResponses);
		response.setTotal(total);
		
		return response;
		
	}
	
	public String updateQuantity(Long cartItemId, int quantity) {

	    CartItem item = cartItemRepository.findById(cartItemId)
	            .orElseThrow(() -> new RuntimeException("Item not found"));

	    if (quantity <= 0) {
	        throw new RuntimeException("Invalid quantity");
	    }

	    Product product = item.getProduct();

	    if (product.getStock() < quantity) {
	        throw new RuntimeException("Insufficient stock");
	    }

	    item.setQuantity(quantity);
	    cartItemRepository.save(item);

	    return "Quantity updated";
	}

	public String removeItem(Long cartItemId) {

	    CartItem item = cartItemRepository.findById(cartItemId)
	            .orElseThrow(() -> new RuntimeException("Item not found"));

	    cartItemRepository.delete(item);

	    return "Item removed";
	}


}
