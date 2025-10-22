package com.microshop.elearningbackend.learning.dto;

public record SaveProgressResponse(
        Long progressId,
        Integer progressPercent,
        Boolean completed
) {}
