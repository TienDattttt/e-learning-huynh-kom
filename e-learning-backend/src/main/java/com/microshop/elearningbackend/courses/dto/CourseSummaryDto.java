package com.microshop.elearningbackend.courses.dto;

import java.time.LocalDateTime;

public record CourseSummaryDto(
        Integer courseId,
        String name,
        String description,
        String image,
        Long price,
        Long promotionPrice,
        Integer categoryId,
        String categoryName,
        Integer teacherId,
        Boolean status,
        LocalDateTime dateCreated,
        Integer students
) {}
