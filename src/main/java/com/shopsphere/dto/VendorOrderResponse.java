package com.shopsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendorOrderResponse {
	
	 private Long orderId;
	    private String customerName;
	    private BigDecimal totalAmount;
	    private String status;
	    private LocalDateTime createdAt;
	    private List<VendorOrderItemResponse> items;

	    public VendorOrderResponse() {}

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public List<VendorOrderItemResponse> getItems() {
			return items;
		}

		public void setItems(List<VendorOrderItemResponse> items) {
			this.items = items;
		}
	    
	    

}
