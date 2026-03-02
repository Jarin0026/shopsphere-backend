package com.shopsphere.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.AddressRequest;
import com.shopsphere.entity.Address;
import com.shopsphere.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
	private final AddressService addressService;

	public AddressController(AddressService addressService) {
		super();
		this.addressService = addressService;
	}
	
	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public String addAddress(@Valid @RequestBody AddressRequest request) {
		return addressService.addAddress(request);
	}
	
	@GetMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<Address> getMyAddresses(){
		return addressService.getMyAddresses();
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
		return addressService.updateAddress(id, request);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String deleteAddress(@PathVariable Long id) {
		return addressService.deleteAddress(id);
	}
	
}
