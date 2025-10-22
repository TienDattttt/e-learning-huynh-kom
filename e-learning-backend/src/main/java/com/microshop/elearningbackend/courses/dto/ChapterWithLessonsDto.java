package com.microshop.elearningbackend.courses.dto;

import java.util.List;

public record ChapterWithLessonsDto(
        Integer chapterId,
        String nameChapter,
        Integer orderChapter,
        List<LessonDto> lessons
) {}
