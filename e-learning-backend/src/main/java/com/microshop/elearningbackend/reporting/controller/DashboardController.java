package com.microshop.elearningbackend.reporting.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.reporting.dto.DashboardResponse;
import com.microshop.elearningbackend.reporting.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @PreAuthorize("hasAnyAuthority('ROLE_Admin','ROLE_GiangVien')")
    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) Integer courseId
    ) {
        return ApiResponse.ok(service.getDashboard(year, month, quarter, courseId));
    }
}
