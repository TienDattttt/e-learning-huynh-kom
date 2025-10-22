package com.microshop.elearningbackend.discounts.dto;

import java.util.List;

public record AttachCoursesRequest(
        Integer discountId,
        List<Integer> courseIds
) {}
