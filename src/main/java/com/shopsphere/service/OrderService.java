package com.shopsphere.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.OrderDetailsResponse;
import com.shopsphere.dto.OrderItemResponse;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.TrackingResponse;
import com.shopsphere.dto.VendorAnalyticsResponse;
import com.shopsphere.dto.VendorOrderItemResponse;
import com.shopsphere.dto.VendorOrderResponse;
import com.shopsphere.entity.Address;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.DeliveryTracking;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.entity.Orderitem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.DeliveryTrackingRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final AddressRepository addressRepository;
	private final DeliveryTrackingRepository trackingRepository;
	private final SimpMessagingTemplate messagingTemplate;


	

	public OrderService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			UserRepository userRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			ProductRepository productRepository, AddressRepository addressRepository,
			DeliveryTrackingRepository trackingRepository, SimpMessagingTemplate messagingTemplate) {
		super();
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
		this.addressRepository = addressRepository;
		this.trackingRepository = trackingRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public String checkout(Long addressId) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart is Empty"));

		Address address = addressRepository.findByIdAndUser(addressId, user)
				.orElseThrow(() -> new RuntimeException("Address not found."));

		List<CartItem> cartItems = cartItemRepository.findByCart(cart);

		if (cartItems.isEmpty()) {
			throw new RuntimeException("Cart is empty.");
		}

		Order order = new Order();
		order.setCustomer(user);
		order.setStatus(OrderStatus.PENDING_PAYMENT);
		order.setCreatedAt(LocalDateTime.now());

		order.setFullName(address.getFullName());
		order.setPhone(address.getPhone());
		order.setStreet(address.getStreet());
		order.setCity(address.getCity());
		order.setState(address.getState());
		order.setZipCode(address.getZipcode());

		List<Orderitem> orderItems = new ArrayList<>();

		BigDecimal total = BigDecimal.ZERO;

		for (CartItem item : cartItems) {
			Product product = item.getProduct();

			if (product.getStock() < item.getQuantity()) {
				throw new RuntimeException("Insufficient stock for " + product.getName());
			}

			Orderitem orderItem = new Orderitem();
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());

			orderItem.setPrice(product.getPrice());
			orderItem.setQuantity(item.getQuantity());

			BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

			orderItem.setSubtotal(subtotal);
			orderItem.setOrder(order);

			total = total.add(subtotal);

			orderItems.add(orderItem);

		}

		order.setTotalAmount(total);
		order.setItems(orderItems);

		orderRepository.save(order);

		return "Order created. Awaiting payment. Order ID: " + order.getId();

	}

	public List<OrderResponse> getMyOrders() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));


	    List<Order> orders = orderRepository.findByCustomer(user);

	    return orders.stream().map(order -> {

	        double total = order.getItems().stream()
	                .mapToDouble(item ->
	                        item.getPrice().doubleValue()
	                        * item.getQuantity())
	                .sum();

	        return new OrderResponse(
	                order.getId(),
	                order.getStatus().name(),
	                total,
	                order.getCreatedAt().toString()
	        );

	    }).toList();
	}


	public OrderDetailsResponse getOrderById(Long id) {

	    Order order = orderRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    List<OrderItemResponse> items = order.getItems().stream()
	            .map(item -> new OrderItemResponse(
	                    item.getProductId(),
	                    item.getProductName(),
	                    item.getQuantity(),
	                    item.getPrice().doubleValue(),
	                    item.getSubtotal().doubleValue()
	            ))
	            .toList();

	    return new OrderDetailsResponse(
	            order.getId(),
	            order.getStatus().name(),
	            order.getTotalAmount().doubleValue(),
	            order.getCreatedAt().toString(),
	            items
	    );
	}

	
	

	@Transactional
	public String cancelOrder(Long orderId) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found."));

		if (!order.getCustomer().getEmail().equals(email)) {
			throw new RuntimeException("Access denied");
		}

		if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
			throw new RuntimeException("Order cannot be cancelled now.");
		}

		for (Orderitem item : order.getItems()) {

			Product product = productRepository.findById(item.getProductId())
			        .orElse(null);


			if (product != null) {
				product.setStock(product.getStock() + item.getQuantity());
				productRepository.save(product);
			}

		}

		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);

		return "Ordered cancelled successfully";

	}

	@Transactional
	public String updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found."));

		OrderStatus currentStatus = order.getStatus();

		if (!isValidTransaction(currentStatus, newStatus)) {
			throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
		}

		order.setStatus(newStatus);
		orderRepository.save(order);
		
		messagingTemplate.convertAndSend(
		        "/topic/orders/" + order.getCustomer().getId(),
		        order.getStatus().name()
		);


		DeliveryTracking tracking = new DeliveryTracking();
		tracking.setOrder(order);
		tracking.setStatus(newStatus);
		tracking.setUpdatedAt(LocalDateTime.now());

		trackingRepository.save(tracking);

		return "Order status updated to " + newStatus;

	}

	public boolean isValidTransaction(OrderStatus current, OrderStatus next) {
		if (current == OrderStatus.PENDING_PAYMENT && next == OrderStatus.CONFIRMED)
			return true;

		if (current == OrderStatus.CONFIRMED && next == OrderStatus.PACKED)
			return true;

		if (current == OrderStatus.PACKED && next == OrderStatus.SHIPPED)
			return true;

		if (current == OrderStatus.SHIPPED && next == OrderStatus.DELIVERED)
			return true;

		if ((current == OrderStatus.PENDING_PAYMENT || current == OrderStatus.CONFIRMED
				|| current == OrderStatus.PACKED) && next == OrderStatus.CANCELLED)
			return true;

		return false;
	}

	public List<TrackingResponse> getOrderTracking(Long orderId) {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

		if (!order.getCustomer().getEmail().equals(email)) {
			throw new RuntimeException("Access denied");
		}

		List<DeliveryTracking> trackingList = trackingRepository.findByOrderOrderByUpdatedAtAsc(order);

		return trackingList.stream().map(t -> new TrackingResponse(t.getStatus().name(), t.getUpdatedAt())).toList();
	}

	public List<VendorOrderResponse> getVendorOrders() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User vendor = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendor not found"));

		List<Order> orders = orderRepository.findOrdersByVendor(vendor);

		return orders.stream().map(order -> {

			VendorOrderResponse response = new VendorOrderResponse();

			response.setOrderId(order.getId());
			response.setCustomerName(order.getCustomer().getName());
			response.setTotalAmount(order.getTotalAmount());
			response.setStatus(order.getStatus().name());
			response.setCreatedAt(order.getCreatedAt());

			List<VendorOrderItemResponse> itemResponses = order.getItems().stream().filter(item -> {
				Product product = productRepository.findById(item.getProductId()).orElse(null);
				return product != null && product.getVendor().getId() == vendor.getId();
			}).map(item -> {

				VendorOrderItemResponse itemDto = new VendorOrderItemResponse();

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

	public BigDecimal getVendorRevenue() {

		List<VendorOrderResponse> orders = getVendorOrders();

		return orders.stream().filter(o -> o.getStatus().equals("CONFIRMED") || o.getStatus().equals("DELIVERED"))
				.map(VendorOrderResponse::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public VendorAnalyticsResponse getVendorAnalytics() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User vendor = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendor not found"));

		List<Order> orders = orderRepository.findOrdersByVendor(vendor);

		VendorAnalyticsResponse response = new VendorAnalyticsResponse();

		// Total Revenue
		BigDecimal totalRevenue = orders.stream()
				.filter(o -> o.getStatus() == OrderStatus.CONFIRMED || o.getStatus() == OrderStatus.DELIVERED)
				.map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		response.setTotalRevenue(totalRevenue);

		// Total Orders
		response.setTotalOrders(orders.size());

		// Status Breakdown
		Map<String, Long> statusMap = orders.stream()
				.collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

		response.setStatusBreakdown(statusMap);

		// Low Stock Products
		long lowStockCount = productRepository.findAll().stream().filter(p -> p.getVendor().getId() == vendor.getId())
				.filter(p -> p.getStock() < 5).count();

		response.setLowStockProducts(lowStockCount);

		return response;
	}
	
	public Map<String, BigDecimal> getMonthlyRevenue() {

	    String email = SecurityContextHolder.getContext().getAuthentication().getName();

	    User vendor = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Vendor not found"));

	    List<Order> orders = orderRepository.findOrdersByVendor(vendor);

	    return orders.stream()
	            .filter(o -> o.getStatus() == OrderStatus.CONFIRMED
	                      || o.getStatus() == OrderStatus.DELIVERED)
	            .collect(Collectors.groupingBy(
	                    o -> o.getCreatedAt().getYear() + "-" +
	                         String.format("%02d", o.getCreatedAt().getMonthValue()),
	                    Collectors.reducing(
	                            BigDecimal.ZERO,
	                            Order::getTotalAmount,
	                            BigDecimal::add
	                    )
	            ));
	}

}
