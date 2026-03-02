package com.shopsphere.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.OrderDetailsResponse;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.TrackingResponse;
import com.shopsphere.entity.DeliveryTracking;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		super();
		this.orderService = orderService;
	}
	
	@PostMapping("/checkout")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String checkout(Long addressId) {
		return orderService.checkout(addressId);
	}
	
	@GetMapping("/my")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<OrderResponse> getMyOrders() {
	    return orderService.getMyOrders();
	}

	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public OrderDetailsResponse getOrder(@PathVariable Long id) {
	    return orderService.getOrderById(id);
	}
	
	@PutMapping("/{id}/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String orderCancel(@PathVariable Long id) {
		return orderService.cancelOrder(id);
	}
	
	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('VENDOR')")
	public String updateOrderStatus(@PathVariable Long id,@RequestParam OrderStatus status) {
		return orderService.updateOrderStatus(id, status);
	}
	
	@GetMapping("/{id}/tracking")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<TrackingResponse> getTracking(@PathVariable Long id) {
	    return orderService.getOrderTracking(id);
	}

	
}
