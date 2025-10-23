package com.microshop.elearningbackend.reporting.dto;

import java.util.List;

public record RevenueSummaryResponse(
        long totalRevenue,
        long successfulOrders,
        List<TopCourseDto> topCourses,
        List<PeriodRevenueDto> byPeriod
) {}
