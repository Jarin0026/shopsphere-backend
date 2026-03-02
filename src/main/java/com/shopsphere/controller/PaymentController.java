package com.shopsphere.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.RazorpayOrderResponse;
import com.shopsphere.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}
	
	@PostMapping("/pay")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String pay(@RequestParam Long orderId, @RequestParam String method) {
		return paymentService.processPayment(orderId, method);
	}
	
	@PostMapping("/create-order")
	@PreAuthorize("hasRole('CUSTOMER')")
	public RazorpayOrderResponse createOrder(@RequestParam Long orderId) throws Exception {
	    return paymentService.createRazorpayOrder(orderId);
	}
	
	@PostMapping("/verify")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String verifyPayment(
	        @RequestParam Long orderId,
	        @RequestParam String razorpayPaymentId,
	        @RequestParam String razorpayOrderId,
	        @RequestParam String razorpaySignature) {

	    return paymentService.verifyAndProcessPayment(
	            orderId,
	            razorpayPaymentId,
	            razorpayOrderId,
	            razorpaySignature
	    );
	}


	
}
