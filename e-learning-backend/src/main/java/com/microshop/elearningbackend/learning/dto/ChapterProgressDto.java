package com.microshop.elearningbackend.learning.dto;

import java.util.List;

public record ChapterProgressDto(
        Integer chapterId,
        String nameChapter,
        Integer orderChapter,
        List<LessonProgressDto> lessons
) {}
