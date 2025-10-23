package com.microshop.elearningbackend.learning.controller;

import com.microshop.elearningbackend.auth.service.CurrentUserService;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService service;
    private final CurrentUserService current;

    // Rule 3: chỉ GET/POST

    /** Xem chi tiết khóa đã mua (kèm progress từng bài) */
    @PreAuthorize("hasAuthority('ROLE_HocVien')")
    @GetMapping("/my-course/detail")
    public ApiResponse<CourseLearningDetailDto> myCourseDetail(@RequestParam Integer courseId) {
        Integer userId = current.requireCurrentUserId();
        return ApiResponse.ok(service.getMyCourseDetail(userId, courseId));
    }

    /** Lưu tiến độ 1 bài học (Upsert) */
    @PreAuthorize("hasAuthority('ROLE_HocVien')")
    @PostMapping("/progress/save")
    public ApiResponse<SaveProgressResponse> saveProgress(@RequestBody SaveProgressRequest req) {
        // Bỏ qua userId client gửi, ép bằng JWT (nếu có userId trong req và khác JWT -> báo lỗi trong service)
        Integer userId = current.requireCurrentUserId();
        return ApiResponse.ok(service.saveProgressFor(userId, req));
    }

    /** Lấy tổng % tiến độ của khóa */
    @PreAuthorize("hasAuthority('ROLE_HocVien')")
    @GetMapping("/progress/get")
    public ApiResponse<Double> getProgress(@RequestParam Integer courseId) {
        Integer userId = current.requireCurrentUserId();
        return ApiResponse.ok(service.getProgress(userId, courseId));
    }
}
