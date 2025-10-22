package com.microshop.elearningbackend.learning.dto;

public record LockAccountRequestDto(
        Integer teacherId,
        Integer studentId,
        String reason
) {}
