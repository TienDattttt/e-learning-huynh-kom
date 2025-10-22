package com.microshop.elearningbackend.courses.dto;

public record PublishCourseRequest(
        Integer courseId,
        Boolean publish
) {}
