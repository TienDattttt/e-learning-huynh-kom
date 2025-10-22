package com.microshop.elearningbackend.discounts.dto;

import java.util.List;

public record DetachCoursesRequest(
        Integer discountId,
        List<Integer> courseIds
) {}
