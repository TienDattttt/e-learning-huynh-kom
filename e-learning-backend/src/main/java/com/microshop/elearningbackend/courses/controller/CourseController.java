package com.microshop.elearningbackend.courses.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.courses.dto.*;
import com.microshop.elearningbackend.courses.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    // Rule 3: chỉ GET/POST

    @PostMapping("/save")
    public ApiResponse<Integer> save(@RequestBody SaveCourseRequest req) {
        return ApiResponse.ok(service.saveCourse(req));
    }

    @PostMapping("/publish")
    public ApiResponse<Integer> publish(@RequestBody PublishCourseRequest req) {
        return ApiResponse.ok(service.publishCourse(req.courseId(), req.publish()));
    }

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

    @PostMapping("/chapters/save")
    public ApiResponse<Integer> saveChapter(@RequestBody SaveChapterRequest req) {
        return ApiResponse.ok(service.saveChapter(req));
    }

    @PostMapping("/lessons/save")
    public ApiResponse<Integer> saveLesson(@RequestBody SaveLessonRequest req) {
        return ApiResponse.ok(service.saveLesson(req));
    }

    @GetMapping("/public/detail")
    public ApiResponse<CourseDetailDto> getPublicDetail(@RequestParam Integer courseId) {
        return ApiResponse.ok(service.getPublicCourseDetail(courseId));
    }
}
