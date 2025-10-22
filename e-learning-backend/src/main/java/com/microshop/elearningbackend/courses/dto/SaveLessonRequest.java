package com.microshop.elearningbackend.courses.dto;

public record SaveLessonRequest(
        Integer courseLessonId,
        Integer chapterId,
        String name,
        String videoPath,
        String slidePath,
        String typeDocument,
        Integer sortOrder
) {}
