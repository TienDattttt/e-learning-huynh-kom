package com.microshop.elearningbackend.orders.repository;

import com.microshop.elearningbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsers_IdAndStatus(Integer userId, String status);
}
