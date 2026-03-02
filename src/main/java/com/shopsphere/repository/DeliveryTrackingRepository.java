package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.DeliveryTracking;
import com.shopsphere.entity.Order;

public interface DeliveryTrackingRepository extends JpaRepository<DeliveryTracking, Long> {
	List<DeliveryTracking> findByOrderOrderByUpdatedAtAsc(Order order);
}
