package com.microshop.elearningbackend.categories.dto;

public record SaveCategoryRequest(
        Integer courseCategoryId,
        String name,
        Integer parentId,
        Integer sortOrder,
        Boolean status
) {}
