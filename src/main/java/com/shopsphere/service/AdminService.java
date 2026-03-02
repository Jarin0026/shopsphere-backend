package com.shopsphere.service;

import com.shopsphere.dto.AdminAnalyticsResponse;
import com.shopsphere.dto.AdminOrderItemResponse;
import com.shopsphere.dto.AdminOrderResponse;
import com.shopsphere.entity.*;
import com.shopsphere.repository.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	

	public AdminService(UserRepository userRepository, OrderRepository orderRepository,
			ProductRepository productRepository, CategoryRepository categoryRepository) {
		super();
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<AdminOrderResponse> getAllOrders() {

		List<Order> orders = orderRepository.findAll();

		return orders.stream().map(order -> {

			AdminOrderResponse response = new AdminOrderResponse();

			response.setOrderId(order.getId());
			response.setCustomerName(order.getCustomer().getName());
			response.setCustomerEmail(order.getCustomer().getEmail());
			response.setStatus(order.getStatus().name());
			response.setTotalAmount(order.getTotalAmount());
			response.setCreatedAt(order.getCreatedAt());

			List<AdminOrderItemResponse> itemResponses = order.getItems().stream().map(item -> {

				AdminOrderItemResponse itemDto = new AdminOrderItemResponse();

				itemDto.setProductId(item.getProductId());
				itemDto.setProductName(item.getProductName());
				itemDto.setQuantity(item.getQuantity());
				itemDto.setPrice(item.getPrice());
				itemDto.setSubtotal(item.getSubtotal());

				return itemDto;
			}).toList();

			response.setItems(itemResponses);

			return response;

		}).toList();
	}

	public AdminAnalyticsResponse getPlatformAnalytics() {

		AdminAnalyticsResponse response = new AdminAnalyticsResponse();

		List<User> users = userRepository.findAll();
		List<Order> orders = orderRepository.findAll();

		response.setTotalUsers(users.size());

		long vendorCount = users.stream().filter(u -> u.getRole() == Role.VENDOR).count();

		response.setTotalVendors(vendorCount);

		response.setTotalOrders(orders.size());

		BigDecimal totalRevenue = orders.stream()
				.filter(o -> o.getStatus() == OrderStatus.CONFIRMED || o.getStatus() == OrderStatus.DELIVERED)
				.map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		response.setTotalRevenue(totalRevenue);

		Map<String, Long> statusMap = orders.stream()
				.collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

		response.setStatusBreakdown(statusMap);

		long lowStockCount = productRepository.findAll().stream().filter(p -> p.getStock() < 5).count();

		response.setLowStockProducts(lowStockCount);

		return response;
	}
	
	public String createCategory(String name) {

	    if (categoryRepository.findByName(name).isPresent()) {
	        throw new RuntimeException("Category already exists");
	    }

	    Category category = new Category();
	    category.setName(name);

	    categoryRepository.save(category);

	    return "Category created successfully";
	}
	
	public List<Category> getAllCategories() {
	    return categoryRepository.findAll();
	}
	
	
	public String deleteCategory(Long id) {

	    Category category = categoryRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Category not found"));

	    categoryRepository.delete(category);

	    return "Category deleted successfully";
	}
	
	public Map<String, BigDecimal> getMonthlyRevenue() {
	    List<Order> orders = orderRepository.findAll();

	    return orders.stream()
	        .filter(o -> o.getStatus() == OrderStatus.CONFIRMED 
	                  || o.getStatus() == OrderStatus.DELIVERED)
	        .collect(Collectors.groupingBy(
	            o -> o.getCreatedAt().getMonth().name(),
	            Collectors.mapping(Order::getTotalAmount,
	                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
	        ));
	}
	
	public List<User> getUsers(String search, String role) {

	    List<User> users = userRepository.findAll();

	    if (search != null && !search.isBlank()) {
	        users = users.stream()
	                .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase())
	                        || u.getEmail().toLowerCase().contains(search.toLowerCase()))
	                .toList();
	    }

	    if (role != null && !role.isBlank()) {
	        users = users.stream()
	                .filter(u -> u.getRole().name().equalsIgnoreCase(role))
	                .toList();
	    }

	    return users;
	}
	
	
	
	public Page<AdminOrderResponse> getOrders(
	        int page,
	        int size,
	        String search,
	        OrderStatus status,
	        String fromDate,
	        String toDate
	) {

	    Pageable pageable = PageRequest.of(page, size);

	    Page<Order> orders;

	    if (search != null && !search.isBlank() && status != null) {

	        orders = orderRepository
	                .findByCustomer_NameContainingIgnoreCaseAndStatus(
	                        search, status, pageable);

	    } else if (search != null && !search.isBlank()) {

	        orders = orderRepository
	                .findByCustomer_NameContainingIgnoreCaseOrCustomer_EmailContainingIgnoreCase(
	                        search, search, pageable);

	    } else if (status != null) {

	        orders = orderRepository.findByStatus(status, pageable);

	    } else {

	        orders = orderRepository.findAll(pageable);
	    }

	    return orders.map(order -> {

	        AdminOrderResponse response = new AdminOrderResponse();

	        response.setOrderId(order.getId());
	        response.setCustomerName(order.getCustomer().getName());
	        response.setCustomerEmail(order.getCustomer().getEmail());
	        response.setStatus(order.getStatus().name());
	        response.setTotalAmount(order.getTotalAmount());
	        response.setCreatedAt(order.getCreatedAt());

	        return response;
	    });
	}
	
	public String toggleUserStatus(Long userId) {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (user.getRole() == Role.ADMIN) {
	        throw new RuntimeException("You cannot disable another admin.");
	    }

	    user.setEnabled(!user.isEnabled());

	    userRepository.save(user);

	    return user.isEnabled()
	            ? "User enabled successfully"
	            : "User disabled successfully";
	}
	
	public String updateOrderStatus(Long orderId, OrderStatus newStatus) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    order.setStatus(newStatus);

	    orderRepository.save(order);

	    return "Order status updated to " + newStatus;
	}
	
	
	
}
