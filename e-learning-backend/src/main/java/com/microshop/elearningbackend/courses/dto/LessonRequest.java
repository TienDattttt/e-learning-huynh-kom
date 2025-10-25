package com.microshop.elearningbackend.courses.dto;

public record LessonRequest(
        Integer courseLessonId,  // null for create
        String name,
        String videoPath,
        String slidePath,
        String typeDocument,
        Integer sortOrder
) {}