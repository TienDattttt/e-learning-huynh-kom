package com.microshop.elearningbackend.learning.dto;

public record SaveProgressRequest(
        Integer userId,
        Integer courseId,
        Integer lessonId,
        Integer progressPercent, // 0..100
        Boolean completed        // optional, true khi hoàn tất
) {}
