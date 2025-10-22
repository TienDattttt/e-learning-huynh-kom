package com.microshop.elearningbackend.courses.dto;

public record SaveCourseRequest(
        Integer courseId,
        String name,
        String description,
        String image,
        String content,
        Long price,
        Long promotionPrice,
        Integer categoryId,
        Integer teacherId   // Auth sẽ làm sau, hiện truyền vào để test
) {}
