package com.microshop.elearningbackend.learning.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StudentProgressDetailDto(
        Integer studentId,
        Integer courseId,
        String courseName,
        int overallPercent,
        LocalDateTime lastUpdated,
        List<LessonProgressItem> lessons
) {
    public record LessonProgressItem(
            Integer lessonId,
            String lessonName,
            Integer progressPercent,
            Boolean completed,
            LocalDateTime updatedAt
    ) {}
}
