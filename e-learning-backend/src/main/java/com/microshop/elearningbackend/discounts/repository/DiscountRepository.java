package com.microshop.elearningbackend.discounts.repository;

import com.microshop.elearningbackend.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    Optional<Discount> findByCodeDiscount(String codeDiscount);
}
