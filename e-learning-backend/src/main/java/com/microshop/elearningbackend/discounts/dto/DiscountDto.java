package com.microshop.elearningbackend.discounts.dto;

import java.time.LocalDateTime;

public record DiscountDto(
        Integer discountId,
        String code,
        Integer percent,
        Long amount,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        boolean active
) {}
