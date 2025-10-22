package com.microshop.elearningbackend.courses.dto;

public record LessonDto(
        Integer lessonId,
        String name,
        String videoPath,
        String slidePath,
        String typeDocument,
        Integer sortOrder
) {}
