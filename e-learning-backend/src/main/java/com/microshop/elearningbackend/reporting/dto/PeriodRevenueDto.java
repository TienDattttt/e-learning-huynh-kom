package com.microshop.elearningbackend.reporting.dto;

public record PeriodRevenueDto(
        String period, // yyyy-MM-dd (DAY) hoặc yyyy-MM (MONTH)
        long   revenue,
        long   orders
) {}
