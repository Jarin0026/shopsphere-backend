package com.shopsphere.controller;

import com.shopsphere.service.AdminService;
import com.shopsphere.dto.AdminAnalyticsResponse;
import com.shopsphere.dto.AdminOrderResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

	/*
	 * @GetMapping("/users") public List<?> getAllUsers() { return
	 * adminService.getAllUsers(); }
	 */

    @GetMapping("/orders")
    public Page<AdminOrderResponse> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return adminService.getOrders(page, size, search, status, fromDate, toDate);
    }


    @GetMapping("/analytics")
    public AdminAnalyticsResponse getAnalytics() {
        return adminService.getPlatformAnalytics();
    }
    
    
    @PostMapping("/categories")
    public String createCategory(@RequestParam String name) {
        return adminService.createCategory(name);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return adminService.getAllCategories();
    }

    @DeleteMapping("/categories/{id}")
    public String deleteCategory(@PathVariable Long id) {
        return adminService.deleteCategory(id);
    }
    
    @GetMapping("/monthly-revenue")
    public Map<String, BigDecimal> getMonthlyRevenue() {
        return adminService.getMonthlyRevenue();
    }
    
    @GetMapping("/users")
    public List<User> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {

        return adminService.getUsers(search, role);
    }
    
    @PutMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id) {
        return adminService.toggleUserStatus(id);
    }
    
    @PutMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return adminService.updateOrderStatus(id, status);
    }
    
   
}
