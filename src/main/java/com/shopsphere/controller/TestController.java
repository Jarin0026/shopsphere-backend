package com.shopsphere.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
	
	@GetMapping("/customer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String customerApi() {
		return "Customer API acccessed";
	}
	
	@GetMapping("/vendor")
	@PreAuthorize("hasRole('VENDOR')")
	public String vendorApi() {
		return "Vendor API accessed";
	}
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String admin() {
		return "Admin API accessed";
	}
	
}
