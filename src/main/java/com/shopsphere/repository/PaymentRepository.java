package com.shopsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	
	
	
}
