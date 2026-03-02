package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderStatus;
import com.shopsphere.entity.User;
;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByCustomer(User user);
	
	@Query("""
			SELECT DISTINCT o FROM Order o
			JOIN o.items i
			JOIN Product p ON p.id = i.productId
			WHERE p.vendor = :vendor
			""")
			List<Order> findOrdersByVendor(@Param("vendor") User vendor);
	
	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

	Page<Order> findByCustomer_NameContainingIgnoreCaseOrCustomer_EmailContainingIgnoreCase(
	        String name,
	        String email,
	        Pageable pageable
	);

	Page<Order> findByCustomer_NameContainingIgnoreCaseAndStatus(
	        String name,
	        OrderStatus status,
	        Pageable pageable
	);

}
