package com.microshop.elearningbackend.courses.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailDto(
        Integer courseId,
        String name,
        String description,
        String image,
        String content,
        Long price,
        Long promotionPrice,
        Integer categoryId,
        Integer teacherId,
        Boolean status,
        LocalDateTime dateCreated,
        List<ChapterWithLessonsDto> chapters
) {}
