package com.microshop.elearningbackend.orders.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.orders.dto.BuyCourseRequest;
import com.microshop.elearningbackend.orders.dto.BuyCourseResponse;
import com.microshop.elearningbackend.orders.dto.MyCourseDto;
import com.microshop.elearningbackend.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService service;

    // Rule 3: chỉ GET/POST

    @PostMapping("/buy")
    public ApiResponse<BuyCourseResponse> buy(@RequestBody BuyCourseRequest req) {
        return ApiResponse.ok(service.buy(req));
    }

    @GetMapping("/my-courses")
    public ApiResponse<List<MyCourseDto>> myCourses(@RequestParam Integer userId) {
        return ApiResponse.ok(service.myCourses(userId));
    }
}
