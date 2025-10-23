package com.microshop.elearningbackend.courses.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.courses.dto.*;
import com.microshop.elearningbackend.courses.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    // Giảng viên tạo/cập nhật khóa học (owner lấy từ JWT)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/save")
    public ApiResponse<Integer> save(@RequestBody SaveCourseRequest req) {
        return ApiResponse.ok(service.saveCourse(req));
    }

    // Giảng viên publish/unpublish khóa học (check owner bên service)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/publish")
    public ApiResponse<Integer> publish(@RequestBody PublishCourseRequest req) {
        return ApiResponse.ok(service.publishCourse(req.courseId(), req.publish()));
    }

    // Danh sách public
    @GetMapping("/public/list")
    public ApiResponse<Page<CourseSummaryDto>> listPublic(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(service.listPublicCourses(categoryId, teacherId, keyword, page, size));
    }

    // Lưu chapter (chỉ GV owner)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/chapters/save")
    public ApiResponse<Integer> saveChapter(@RequestBody SaveChapterRequest req) {
        return ApiResponse.ok(service.saveChapter(req));
    }

    // Lưu lesson (chỉ GV owner)
    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping("/lessons/save")
    public ApiResponse<Integer> saveLesson(@RequestBody SaveLessonRequest req) {
        return ApiResponse.ok(service.saveLesson(req));
    }

    // Chi tiết public
    @GetMapping("/public/detail")
    public ApiResponse<CourseDetailDto> getPublicDetail(@RequestParam Integer courseId) {
        return ApiResponse.ok(service.getPublicCourseDetail(courseId));
    }
}
