package com.microshop.elearningbackend.orders.dto;

public record BuyCourseResponse(
        Long orderId,
        String status,     // "SUCCESS" | "ALREADY_OWNED"
        Long totalAmount
) {}
