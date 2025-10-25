package com.microshop.elearningbackend.courses.dto;

import java.util.List;

public record ChapterRequest(
        Integer chapterId,  // null for create
        String nameChapter,
        Integer orderChapter,
        List<LessonRequest> lessons
) {}