package com.shopsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Orderitem;

public interface OrderItemRepository extends JpaRepository<Orderitem, Long> {

}
