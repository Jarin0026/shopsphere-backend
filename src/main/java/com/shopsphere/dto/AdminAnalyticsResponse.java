package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AdminAnalyticsResponse {

    private long totalUsers;
    private long totalVendors;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> statusBreakdown;
    private long lowStockProducts;

    public AdminAnalyticsResponse() {}

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getTotalVendors() {
		return totalVendors;
	}

	public void setTotalVendors(long totalVendors) {
		this.totalVendors = totalVendors;
	}

	public long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Map<String, Long> getStatusBreakdown() {
		return statusBreakdown;
	}

	public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
		this.statusBreakdown = statusBreakdown;
	}

	public long getLowStockProducts() {
		return lowStockProducts;
	}

	public void setLowStockProducts(long lowStockProducts) {
		this.lowStockProducts = lowStockProducts;
	}

    
}
