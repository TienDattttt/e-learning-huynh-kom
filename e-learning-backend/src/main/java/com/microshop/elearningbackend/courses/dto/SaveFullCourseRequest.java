package com.microshop.elearningbackend.courses.dto;

import java.util.List;

public record SaveFullCourseRequest(
        Integer courseId,  // null for create
        String name,
        String description,
        String image,
        String content,
        Long price,
        Long promotionPrice,
        Integer categoryId,
        Boolean publish,
        List<ChapterRequest> chapters
) {}
