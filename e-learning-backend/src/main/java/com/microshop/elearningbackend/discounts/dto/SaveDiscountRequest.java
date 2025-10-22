package com.microshop.elearningbackend.discounts.dto;

import java.time.LocalDateTime;

public record SaveDiscountRequest(
        Integer discountId,       // null = create
        String code,              // required & unique
        Integer percent,          // XOR with amount
        Long amount,              // XOR with percent
        LocalDateTime fromDate,   // optional
        LocalDateTime toDate      // optional
) {}
