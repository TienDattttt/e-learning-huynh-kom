package com.microshop.elearningbackend.courses.controller;

import com.microshop.elearningbackend.auth.service.CurrentUserService;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.courses.dto.*;
import com.microshop.elearningbackend.courses.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;
    private final CurrentUserService current;

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

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/teacher/list")
    public ApiResponse<Page<CourseSummaryDto>> listTeacherCourses(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Integer teacherId = current.requireCurrentUserId();
        return ApiResponse.ok(service.listTeacherCourses(teacherId, page, size));
    }

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @GetMapping("/teacher/detail")
    public ApiResponse<CourseDetailDto> getTeacherDetail(@RequestParam Integer courseId) {
        return ApiResponse.ok(service.getTeacherCourseDetail(courseId));
    }

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Integer id) {
        service.deleteCourse(id);
        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @DeleteMapping("/chapters/{id}")
    public ApiResponse<Void> deleteChapter(@PathVariable Integer id) {
        service.deleteChapter(id);
        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @DeleteMapping("/lessons/{id}")
    public ApiResponse<Void> deleteLesson(@PathVariable Integer id) {
        service.deleteLesson(id);
        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasAuthority('ROLE_GiangVien')")
    @PostMapping(value = "/full-save", consumes = {"multipart/form-data"})  // Thêm consumes multipart
    public ApiResponse<Integer> fullSave(
            @RequestPart("courseData") SaveFullCourseRequest req,  // Phần JSON
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile  // Phần file optional
    ) {
        return ApiResponse.ok(service.saveFullCourse(req, imageFile));  // Gọi service với file
    }
}