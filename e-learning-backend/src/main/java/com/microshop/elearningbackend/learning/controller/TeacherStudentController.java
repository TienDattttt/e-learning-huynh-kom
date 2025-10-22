package com.microshop.elearningbackend.learning.controller;

import com.microshop.elearningbackend.common.ApiPage;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.service.TeacherStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherStudentController {

    private final TeacherStudentService service;

    // Bước 2: danh sách học viên theo khóa (có thể bỏ courseId để xem toàn bộ học viên của GV)
    @GetMapping("/students")
    public ApiResponse<ApiPage<StudentListItemDto>> listStudents(@RequestParam Integer teacherId,
                                                                 @RequestParam(required = false) Integer courseId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.listStudents(teacherId, courseId, page, size));
    }

    // Bước 3: xem chi tiết tiến độ 1 học viên trong 1 khóa
    @GetMapping("/students/{studentId}/progress")
    public ApiResponse<StudentProgressDetailDto> studentProgress(@RequestParam Integer teacherId,
                                                                 @RequestParam Integer courseId,
                                                                 @PathVariable Integer studentId) {
        return ApiResponse.ok(service.studentProgressDetail(teacherId, courseId, studentId));
    }

    // Bước 4: gửi yêu cầu khóa tài khoản đến Admin (PENDING)
    @PostMapping("/students/lock-request")
    public ApiResponse<LockAccountResponseDto> lockRequest(@RequestBody LockAccountRequestDto req) {
        return ApiResponse.ok(service.requestLockAccount(req));
    }
}
