package com.microshop.elearningbackend.learning.dto;

public record LessonProgressDto(
        Integer lessonId,
        String name,
        String videoPath,
        String slidePath,
        String typeDocument,
        Integer sortOrder,
        Integer progressPercent, // 0..100
        Boolean completed
) {}
