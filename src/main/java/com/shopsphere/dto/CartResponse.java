package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
	private List<CartItemResponse> items;
	private BigDecimal total;
	
	public CartResponse() {}

	public List<CartItemResponse> getItems() {
		return items;
	}

	public void setItems(List<CartItemResponse> items) {
		this.items = items;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	
}
