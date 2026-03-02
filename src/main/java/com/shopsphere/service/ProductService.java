package com.shopsphere.service;

import com.cloudinary.Cloudinary;
import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.*;
import com.shopsphere.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final Cloudinary cloudinary;
	private final CartItemRepository cartItemRepository;
	private final WishlistRepository wishlistRepository;
	
	
	

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			UserRepository userRepository, Cloudinary cloudinary, CartItemRepository cartItemRepository,
			WishlistRepository wishlistRepository) {
		super();
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.cloudinary = cloudinary;
		this.cartItemRepository = cartItemRepository;
		this.wishlistRepository = wishlistRepository;
	}

	public ProductResponse createProduct(String name, String description, BigDecimal price, int stock, Long categoryId,
			List<MultipartFile> images) {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User vendor = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendor not found"));

		if (!vendor.getRole().name().equals("VENDOR")) {
			throw new RuntimeException("Only vendors can create products");
		}

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		try {

			Product product = new Product();
			product.setName(name);
			product.setDescription(description);
			product.setPrice(price);
			product.setStock(stock);
			product.setCategory(category);
			product.setVendor(vendor);

			List<ProductImage> imageList = new ArrayList<>();

			for (MultipartFile file : images) {

				Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), new HashMap<>());

				String imageUrl = uploadResult.get("secure_url").toString();
				String publicId = uploadResult.get("public_id").toString();

				ProductImage productImage = new ProductImage();
				productImage.setImageUrl(imageUrl);
				productImage.setProduct(product);
				productImage.setPublicId(publicId);

				imageList.add(productImage);
			}

			product.setImages(imageList);

			productRepository.save(product);

			return mapToResponse(product);

		} catch (Exception e) {
			throw new RuntimeException("Image upload failed: " + e.getMessage());
		}
	}

	public Page<ProductResponse> getAllProducts(Long categoryId, String search, String sortBy, String direction,
			int page, int size) {

		Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categoryId))
				.and(ProductSpecification.nameContains(search));

		Page<Product> product = productRepository.findAll(spec, pageable);

		return product.map(this::mapToResponse);

	}

	public ProductResponse mapToResponse(Product product) {

		ProductResponse response = new ProductResponse();
		response.setId(product.getId());
		response.setName(product.getName());
		response.setDescription(product.getDescription());
		response.setPrice(product.getPrice());
		response.setStock(product.getStock());
		response.setCategoryName(product.getCategory().getName());
		response.setCategoryId(product.getCategory().getId());
		response.setVendorName(product.getVendor().getName());
		response.setImageUrls(product.getImages().stream().map(ProductImage::getImageUrl).toList());

		return response;
	}

	public ProductResponse updateProduct(
	        Long productId,
	        String name,
	        String description,
	        BigDecimal price,
	        int stock,
	        Long categoryId,
	        List<MultipartFile> images) {

	    String email = SecurityContextHolder.getContext()
	            .getAuthentication().getName();

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found."));

	    if (!product.getVendor().getEmail().equals(email)) {
	        throw new RuntimeException("You can only update your own product.");
	    }

	    Category category = categoryRepository.findById(categoryId)
	            .orElseThrow(() -> new RuntimeException("Category not found."));

	    product.setName(name);
	    product.setDescription(description);
	    product.setPrice(price);
	    product.setStock(stock);
	    product.setCategory(category);

	    try {

	        // If new images are provided → replace old ones
	    	if (images != null && !images.isEmpty()) {

	    	    // Delete old images from Cloudinary
	    	    for (ProductImage oldImage : product.getImages()) {
	    	        cloudinary.uploader().destroy(oldImage.getPublicId(), Map.of());
	    	    }

	    	    // REMOVE old images from DB safely
	    	    product.getImages().clear();

	    	    for (MultipartFile file : images) {

	    	        Map<String, Object> uploadResult =
	    	                cloudinary.uploader().upload(file.getBytes(), Map.of());

	    	        String imageUrl = uploadResult.get("secure_url").toString();
	    	        String publicId = uploadResult.get("public_id").toString();

	    	        ProductImage newImage = new ProductImage();
	    	        newImage.setImageUrl(imageUrl);
	    	        newImage.setPublicId(publicId);
	    	        newImage.setProduct(product);

	    	        product.getImages().add(newImage); // ✅ add to existing collection
	    	    }
	    	}

	        productRepository.save(product);

	        return mapToResponse(product);

	    } catch (Exception e) {
	        throw new RuntimeException("Product update failed: " + e.getMessage());
	    }
	}


	@Transactional
	public String deleteProduct(Long productId) {

	    String email = SecurityContextHolder.getContext()
	            .getAuthentication().getName();

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found."));

	    if (!product.getVendor().getEmail().equals(email)) {
	        throw new RuntimeException("You can only delete your own product.");
	    }

	    try {

	        
	        wishlistRepository.deleteByProduct(product);

	        
	        cartItemRepository.deleteByProduct(product);

	        
	        if (product.getImages() != null) {
	            for (ProductImage image : product.getImages()) {
	                if (image.getPublicId() != null) {
	                    cloudinary.uploader().destroy(image.getPublicId(), Map.of());
	                }
	            }
	        }

	        
	        productRepository.delete(product);

	        return "Product deleted successfully";

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to delete product: " + e.getMessage());
	    }
	}

	public Page<ProductResponse> getVendorProducts(
	        int page,
	        int size,
	        String search,
	        Long categoryId) {

	    String email = SecurityContextHolder.getContext().getAuthentication().getName();

	    User vendor = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Vendor not found"));

	    Pageable pageable = PageRequest.of(page, size);

	    Specification<Product> spec =
	            Specification.where(ProductSpecification.hasVendor(vendor.getId()))
	                    .and(ProductSpecification.nameContains(search))
	                    .and(ProductSpecification.hasCategory(categoryId));

	    Page<Product> products = productRepository.findAll(spec, pageable);

	    return products.map(this::mapToResponse);
	}

	public ProductResponse getProductById(Long id) {

		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		return mapToResponse(product);
	}

}
