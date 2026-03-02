package com.shopsphere.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.ProductResponse;
import com.shopsphere.dto.VendorAnalyticsResponse;
import com.shopsphere.dto.VendorOrderResponse;
import com.shopsphere.entity.Order;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.service.OrderService;
import com.shopsphere.service.ProductService;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

	private final ProductService productService;

	public VendorController(ProductService productService, OrderRepository orderRepository, OrderService orderService) {
		super();
		this.productService = productService;
		this.orderRepository = orderRepository;
		this.orderService = orderService;
	}
	
	@GetMapping("/products")
	@PreAuthorize("hasRole('VENDOR')")
	public Page<ProductResponse> getVendorProducts(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) Long categoryId
	) {
	    return productService.getVendorProducts(page, size, search, categoryId);
	}
	
	@GetMapping("/orders")
	@PreAuthorize("hasRole('VENDOR')")
	public List<VendorOrderResponse> getVendorOrders() {
	    return orderService.getVendorOrders();
	}

	
	@GetMapping("/revenue")
	@PreAuthorize("hasRole('VENDOR')")
	public BigDecimal getVendorRevenue() {
	    return orderService.getVendorRevenue();
	}
	
	@GetMapping("/analytics")
	@PreAuthorize("hasRole('VENDOR')")
	public VendorAnalyticsResponse getAnalytics() {
	    return orderService.getVendorAnalytics();
	}
	
	
	@GetMapping("/monthly-revenue")
	@PreAuthorize("hasRole('VENDOR')")
	public Map<String, BigDecimal> getMonthlyRevenue() {
	    return orderService.getMonthlyRevenue();
	}


	
}
