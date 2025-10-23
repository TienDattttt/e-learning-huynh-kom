package com.microshop.elearningbackend.orders.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.orders.dto.BuyCourseRequest;
import com.microshop.elearningbackend.orders.dto.BuyCourseResponse;
import com.microshop.elearningbackend.orders.dto.MyCourseDto;
import com.microshop.elearningbackend.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService service;

    // (Giữ nguyên nếu bạn đã có — đây là mua khóa học)
    @PreAuthorize("hasAuthority('ROLE_HocVien')")
    @PostMapping("/buy")
    public ApiResponse<BuyCourseResponse> buy(@RequestBody BuyCourseRequest req) {
        return ApiResponse.ok(service.buyForCurrentUser(req)); // dùng user từ JWT
    }

    // Học viên xem danh sách khóa đã mua — KHÔNG truyền userId nữa
    @PreAuthorize("hasAuthority('ROLE_HocVien')")
    @GetMapping("/my-courses")
    public ApiResponse<List<MyCourseDto>> myCourses() {
        return ApiResponse.ok(service.myCoursesForCurrentUser());
    }
}
