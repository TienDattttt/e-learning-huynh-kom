package com.microshop.elearningbackend.learning.dto;

import java.time.LocalDateTime;

public record LockAccountResponseDto(
        Long requestId,
        String status,               // PENDING
        LocalDateTime createdAt
) {}
