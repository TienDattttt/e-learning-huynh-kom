package com.microshop.elearningbackend.learning.controller;

import com.microshop.elearningbackend.auth.service.CurrentUserService;
import com.microshop.elearningbackend.common.ApiPage;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.learning.dto.*;
import com.microshop.elearningbackend.learning.service.TeacherStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherStudentController {

    private final TeacherStudentService service;
    private final CurrentUserService current;

    // Bước 2: danh sách học viên theo khóa (courseId có thể null)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/students")
    public ApiResponse<ApiPage<StudentListItemDto>> listStudents(@RequestParam(required = false) Integer courseId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        Integer teacherId = current.requireCurrentUserId();
        return ApiResponse.ok(service.listStudents(teacherId, courseId, page, size));
    }

    // Bước 3: xem chi tiết tiến độ 1 học viên trong 1 khóa
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/students/{studentId}/progress")
    public ApiResponse<StudentProgressDetailDto> studentProgress(@RequestParam Integer courseId,
                                                                 @PathVariable Integer studentId) {
        Integer teacherId = current.requireCurrentUserId();
        return ApiResponse.ok(service.studentProgressDetail(teacherId, courseId, studentId));
    }

    // Bước 4: gửi yêu cầu khóa tài khoản đến Admin (PENDING)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/students/lock-request")
    public ApiResponse<LockAccountResponseDto> lockRequest(@RequestBody LockAccountRequestDto req) {
        Integer teacherId = current.requireCurrentUserId();
        // ép teacherId từ JWT vào request (tránh giả mạo)
        LockAccountRequestDto fixed = new LockAccountRequestDto(teacherId, req.studentId(), req.reason());
        return ApiResponse.ok(service.requestLockAccount(fixed));
    }
}
