package com.microshop.elearningbackend.orders.dto;

import java.time.LocalDateTime;

public record MyCourseDto(
        Integer courseId,
        String name,
        String image,
        Integer teacherId,
        LocalDateTime purchasedAt
) {}
