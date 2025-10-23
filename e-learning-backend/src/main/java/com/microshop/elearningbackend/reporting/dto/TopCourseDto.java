package com.microshop.elearningbackend.reporting.dto;

public record TopCourseDto(
        Integer courseId,
        String  courseName,
        long    revenue,
        long    orders
) {}
