package com.shopsphere.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.shopsphere.dto.RazorpayOrderResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.DeliveryTracking;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.entity.Orderitem;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.PaymentStatus;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.DeliveryTrackingRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final ProductRepository productRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final DeliveryTrackingRepository trackingRepository;
	private final RazorpayClient razorpayClient;
	
	@Value("${razorpay.key.secret}")
	private String razorpaySecret;

	
	


	

	public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository,
			ProductRepository productRepository, CartRepository cartRepository, CartItemRepository cartItemRepository,
			UserRepository userRepository, DeliveryTrackingRepository trackingRepository,
			RazorpayClient razorpayClient) {
		super();
		this.orderRepository = orderRepository;
		this.paymentRepository = paymentRepository;
		this.productRepository = productRepository;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.trackingRepository = trackingRepository;
		this.razorpayClient = razorpayClient;
	}


	@Transactional
	public String processPayment(Long orderId, String paymentMethod) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found."));

		if (!order.getCustomer().getEmail().equals(email)) {
			throw new RuntimeException("Access Denied");
		}

		if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
			throw new RuntimeException("Order is not awaiting payment");
		}

		// recheck stock
		for (Orderitem item : order.getItems()) {
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (product.getStock() < item.getQuantity()) {
				order.setStatus(OrderStatus.CANCELLED);
				orderRepository.save(order);

				createPayment(order, paymentMethod, PaymentStatus.FAILED);

				return "Payment failed due to insufficient stock. Order Cancelled";
			}

		}

		// Reduce Stock
		for (Orderitem item : order.getItems()) {

			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			product.setStock(product.getStock() - item.getQuantity());
			productRepository.save(product);
		}

		// Clear cart
		Cart cart = cartRepository.findByUser(user).orElse(null);
		if (cart != null) {
			cartItemRepository.deleteAll(cartItemRepository.findByCart(cart));
		}

		order.setStatus(OrderStatus.CONFIRMED);
		orderRepository.save(order);

		DeliveryTracking tracking = new DeliveryTracking();
		tracking.setOrder(order);
		tracking.setStatus(OrderStatus.CONFIRMED);
		tracking.setUpdatedAt(LocalDateTime.now());

		trackingRepository.save(tracking);

		createPayment(order, paymentMethod, PaymentStatus.SUCCESS);

		return "Payment successfully. Order confirmed";

	}


	private void createPayment(Order order, String paymentMethod, PaymentStatus status) {

		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(paymentMethod);
		payment.setStatus(status);
		payment.setCreatedAt(LocalDateTime.now());

		paymentRepository.save(payment);
	}
	
	public RazorpayOrderResponse createRazorpayOrder(Long orderId) throws Exception {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    int amountInPaise = order.getTotalAmount().multiply(new BigDecimal(100)).intValue();

	    JSONObject options = new JSONObject();
	    options.put("amount", amountInPaise);
	    options.put("currency", "INR");
	    options.put("receipt", "order_rcptid_" + orderId);

	    com.razorpay.Order razorpayOrder =
	            razorpayClient.orders.create(options);

	    return new RazorpayOrderResponse(
	            orderId.toString(),
	            razorpayOrder.get("id"),
	            amountInPaise,
	            "INR"
	    );
	}
	
	
	@Transactional
	public String verifyAndProcessPayment(
	        Long orderId,
	        String razorpayPaymentId,
	        String razorpayOrderId,
	        String razorpaySignature) {

	    try {

	        // 1️⃣ Verify signature
	        String payload = razorpayOrderId + "|" + razorpayPaymentId;

	        boolean isValid = Utils.verifySignature(
	                payload,
	                razorpaySignature,
	                razorpaySecret
	        );


	        if (!isValid) {
	            throw new RuntimeException("Invalid payment signature");
	        }

	        // 2️⃣ Fetch order
	        String email = SecurityContextHolder.getContext()
	                .getAuthentication().getName();

	        Order order = orderRepository.findById(orderId)
	                .orElseThrow(() -> new RuntimeException("Order not found"));

	        if (!order.getCustomer().getEmail().equals(email)) {
	            throw new RuntimeException("Access Denied");
	        }

	        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
	            throw new RuntimeException("Order already processed");
	        }

	        // 3️⃣ Recheck stock
	        for (Orderitem item : order.getItems()) {

	            Product product = productRepository.findById(item.getProductId())
	                    .orElseThrow(() -> new RuntimeException("Product not found"));

	            if (product.getStock() < item.getQuantity()) {
	                order.setStatus(OrderStatus.CANCELLED);
	                orderRepository.save(order);

	                createPayment(order, "RAZORPAY", PaymentStatus.FAILED);

	                return "Payment failed due to insufficient stock";
	            }
	        }

	        // 4️⃣ Reduce stock
	        for (Orderitem item : order.getItems()) {

	            Product product = productRepository.findById(item.getProductId())
	                    .orElseThrow(() -> new RuntimeException("Product not found"));

	            product.setStock(product.getStock() - item.getQuantity());
	            productRepository.save(product);
	        }

	        // 5️⃣ Clear cart
	        User user = order.getCustomer();
	        Cart cart = cartRepository.findByUser(user).orElse(null);

	        if (cart != null) {
	            cartItemRepository.deleteAll(
	                    cartItemRepository.findByCart(cart)
	            );
	        }

	        // 6️⃣ Confirm order
	        order.setStatus(OrderStatus.CONFIRMED);
	        orderRepository.save(order);

	        // 7️⃣ Create tracking
	        DeliveryTracking tracking = new DeliveryTracking();
	        tracking.setOrder(order);
	        tracking.setStatus(OrderStatus.CONFIRMED);
	        tracking.setUpdatedAt(LocalDateTime.now());
	        trackingRepository.save(tracking);

	        // 8️⃣ Save payment record
	        createPayment(order, "RAZORPAY", PaymentStatus.SUCCESS);

	        return "Payment successful. Order confirmed.";

	    } catch (Exception e) {
	        throw new RuntimeException("Payment verification failed: " + e.getMessage());
	    }
	}



}
