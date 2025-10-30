package com.microshop.elearningbackend.reporting.dto;

import java.util.List;

public record DashboardResponse(
        DashboardSummaryDto summary,
        List<RevenueTrendDto> trend,
        List<TopCourseDto> topCourses
) {}
