package com.microshop.elearningbackend.orders.dto;

public record BuyCourseRequest(
        Integer userId,
        Integer courseId,
        String payMethod,      // "AUTO" (tạm)
        String voucherCode     // có thể null
) {}
