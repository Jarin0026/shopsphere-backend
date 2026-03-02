package com.shopsphere.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.CartResponse;
import com.shopsphere.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;

	public CartController(CartService cartService) {
		super();
		this.cartService = cartService;
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
		return cartService.addToCart(productId, quantity);
	}
	
	@GetMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public CartResponse viewCart() {
		return cartService.viewCart();
	}
	
	@PutMapping("/update")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String updateQuantity(
	        @RequestParam Long cartItemId,
	        @RequestParam int quantity) {
	    return cartService.updateQuantity(cartItemId, quantity);
	}

	@DeleteMapping("/remove/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String removeItem(@PathVariable Long id) {
	    return cartService.removeItem(id);
	}

	
	
}