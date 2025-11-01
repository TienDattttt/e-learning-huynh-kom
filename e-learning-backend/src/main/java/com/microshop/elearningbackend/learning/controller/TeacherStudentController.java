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

    // 🎓 Giảng viên: danh sách học viên
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/students")
    public ApiResponse<ApiPage<StudentListItemDto>> listStudents(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Integer teacherId = current.requireCurrentUserId();
        return ApiResponse.ok(service.listStudents(teacherId, courseId, page, size));
    }

    // 📊 Giảng viên: xem chi tiết tiến độ học viên
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/students/{studentId}/progress")
    public ApiResponse<StudentProgressDetailDto> studentProgress(
            @RequestParam Integer courseId,
            @PathVariable Integer studentId) {
        Integer teacherId = current.requireCurrentUserId();
        return ApiResponse.ok(service.studentProgressDetail(teacherId, courseId, studentId));
    }

    // 🔒 Giảng viên: gửi yêu cầu khóa học viên
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/students/lock-request")
    public ApiResponse<?> createLock(@RequestBody LockAccountRequestDto req) {
        Integer teacherId = current.requireCurrentUserId();
        return service.createLockRequest(teacherId, req.getStudentId(), req.getReason());
    }

    // 🧑‍💼 Admin: xem danh sách yêu cầu khóa
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @GetMapping("/lock-requests/pending")
    public ApiResponse<?> pendingLocks() {
        return service.listPendingLockRequests();
    }

    // 🧑‍💼 Admin: duyệt hoặc từ chối yêu cầu
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PutMapping("/lock-requests/{id}")
    public ApiResponse<?> updateLockStatus(
            @PathVariable long id,
            @RequestParam String status) {
        return service.updateLockStatus(id, status);
    }
}
