package com.microshop.elearningbackend.learning.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService service;

    // Rule 3: chỉ GET/POST

    /** Xem chi tiết khóa đã mua (kèm progress từng bài) */
    @GetMapping("/my-course/detail")
    public ApiResponse<CourseLearningDetailDto> myCourseDetail(@RequestParam Integer userId,
                                                               @RequestParam Integer courseId) {
        return ApiResponse.ok(service.getMyCourseDetail(userId, courseId));
    }

    /** Lưu tiến độ 1 bài học (Upsert) */
    @PostMapping("/progress/save")
    public ApiResponse<SaveProgressResponse> saveProgress(@RequestBody SaveProgressRequest req) {
        return ApiResponse.ok(service.saveProgress(req));
    }

    /** Lấy tổng % tiến độ của khóa */
    @GetMapping("/progress/get")
    public ApiResponse<Double> getProgress(@RequestParam Integer userId,
                                           @RequestParam Integer courseId) {
        return ApiResponse.ok(service.getProgress(userId, courseId));
    }
}
