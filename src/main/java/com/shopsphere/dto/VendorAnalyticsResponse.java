package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.Map;

public class VendorAnalyticsResponse {

	 private BigDecimal totalRevenue;
	    private long totalOrders;
	    private Map<String, Long> statusBreakdown;
	    private long lowStockProducts;

	    public VendorAnalyticsResponse() {}

		public BigDecimal getTotalRevenue() {
			return totalRevenue;
		}

		public void setTotalRevenue(BigDecimal totalRevenue) {
			this.totalRevenue = totalRevenue;
		}

		public long getTotalOrders() {
			return totalOrders;
		}

		public void setTotalOrders(long totalOrders) {
			this.totalOrders = totalOrders;
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
