package com.shopsphere.dto;

public class RazorpayOrderResponse {

	private String orderId;
	private String razorpayOrderId;
	private int amount;
	private String currency;

	public RazorpayOrderResponse(String orderId, String razorpayOrderId, int amount, String currency) {
		super();
		this.orderId = orderId;
		this.razorpayOrderId = razorpayOrderId;
		this.amount = amount;
		this.currency = currency;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
