package com.microshop.elearningbackend.courses.dto;

public record SaveChapterRequest(
        Integer chapterId,
        Integer courseId,
        String nameChapter,
        Integer orderChapter

) {}
