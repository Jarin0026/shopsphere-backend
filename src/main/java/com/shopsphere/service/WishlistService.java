package com.shopsphere.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.Wishlist;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.repository.WishlistRepository;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    
    
    
    public WishlistService(WishlistRepository wishlistRepository, UserRepository userRepository,
			ProductRepository productRepository, ProductService productService) {
		super();
		this.wishlistRepository = wishlistRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.productService = productService;
	}

	public String toggleWishlist(Long productId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Wishlist> existing =
                wishlistRepository.findByUserAndProduct(user, product);

        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return "Removed from wishlist";
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);

        return "Added to wishlist";
    }
    
	public List<ProductResponse> getWishlist() {

	    String email = SecurityContextHolder.getContext()
	            .getAuthentication().getName();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    List<Wishlist> wishlistItems =
	            wishlistRepository.findByUser(user);

	    return wishlistItems.stream()
	            .map(w -> productService.mapToResponse(w.getProduct()))
	            .toList();
	}



    
    
}
