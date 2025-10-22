package com.microshop.elearningbackend.learning.dto;

import java.time.LocalDateTime;

public record StudentListItemDto(
        Integer studentId,
        String fullName,
        String email,
        Integer courseId,
        String courseName,
        Integer progressPercent,
        LocalDateTime lastUpdated
) {}
