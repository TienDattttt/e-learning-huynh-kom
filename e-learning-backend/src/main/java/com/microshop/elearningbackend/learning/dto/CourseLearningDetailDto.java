package com.microshop.elearningbackend.learning.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CourseLearningDetailDto(
        Integer courseId,
        String name,
        String description,
        String image,
        Integer teacherId,
        Boolean status,
        LocalDateTime dateCreated,
        Double overallProgress, // 0..100
        List<ChapterProgressDto> chapters
) {}
