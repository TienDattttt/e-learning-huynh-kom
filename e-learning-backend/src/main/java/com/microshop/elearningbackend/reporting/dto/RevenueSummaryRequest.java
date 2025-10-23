package com.microshop.elearningbackend.reporting.dto;

import java.time.LocalDateTime;

public record RevenueSummaryRequest(
        LocalDateTime start,          // nullable -> default 30 ngày gần nhất
        LocalDateTime end,            // nullable
        Integer courseId,             // nullable -> all courses
        Integer teacherId,            // nullable -> Admin có thể bỏ trống, GV truyền userId của mình
        GroupBy groupBy               // DAY hoặc MONTH (nullable -> DAY)
) {
    public enum GroupBy { DAY, MONTH }
}
