package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Address;
import com.shopsphere.entity.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUser(User user);
	
	Optional<Address> findByIdAndUser(Long id, User user);
	
}
