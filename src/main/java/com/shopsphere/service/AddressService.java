package com.shopsphere.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.AddressRequest;
import com.shopsphere.entity.Address;
import com.shopsphere.entity.User;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.UserRepository;

@Service
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
		super();
		this.addressRepository = addressRepository;
		this.userRepository = userRepository;
	}

	public String addAddress(AddressRequest request) {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.getRole().name().equals("CUSTOMER")) {
			throw new RuntimeException("Only customers can add address.");
		}

		if (request.isDefault()) {
			addressRepository.findByUser(user).forEach(addr -> {
				addr.setDefault(false);
				addressRepository.save(addr);
			});
		}

		Address address = new Address();
		address.setFullName(request.getFullName());
		address.setPhone(request.getPhone());
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setZipcode(request.getZipCode());
		address.setDefault(request.isDefault());
		address.setUser(user);

		addressRepository.save(address);

		return "Address saved successfully";

	}

	public List<Address> getMyAddresses() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
		return addressRepository.findByUser(user);
	}

	public String updateAddress(Long id, AddressRequest request) {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

		Address address = addressRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new RuntimeException("Address not found."));

		if (request.isDefault()) {
			addressRepository.findByUser(user).forEach(addr -> {
				addr.setDefault(false);
				addressRepository.save(addr);
			});
		}

		 address.setFullName(request.getFullName());
		    address.setPhone(request.getPhone());
		    address.setStreet(request.getStreet());
		    address.setCity(request.getCity());
		    address.setState(request.getState());
		    address.setZipcode(request.getZipCode());
		    address.setDefault(request.isDefault());

		    addressRepository.save(address);

		    return "Address updated successfully";
		
	}
	
	public String deleteAddress(Long id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
		
		Address address = addressRepository.findByIdAndUser(id, user).orElseThrow(()-> new RuntimeException("Address not found"));
		
		addressRepository.delete(address);
		
		return "Address deleted successfully";
		
	}

}
