package com.microshop.elearningbackend.discounts.controller;

import com.microshop.elearningbackend.common.ApiPage;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.discounts.dto.*;
import com.microshop.elearningbackend.discounts.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountsController {

    private final DiscountService service;

    // Rule 3: chỉ GET/POST

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PostMapping("/save")
    public ApiResponse<DiscountDto> save(@RequestBody SaveDiscountRequest req) {
        return ApiResponse.ok(service.save(req));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_GiangVien')")
    @GetMapping("/list")
    public ApiResponse<ApiPage<DiscountDto>> list(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) Boolean activeOnly,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.list(q, activeOnly, page, size));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_GiangVien')")
    @PostMapping("/attach-courses")
    public ApiResponse<Void> attach(@RequestBody AttachCoursesRequest req) {
        service.attachCourses(req);
        return ApiResponse.ok();
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PostMapping("/detach-courses")
    public ApiResponse<Void> detach(@RequestBody DetachCoursesRequest req) {
        service.detachCourses(req);
        return ApiResponse.ok();
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @GetMapping("/{discountId}/courses")
    public ApiResponse<List<Integer>> listCourses(@PathVariable Integer discountId) {
        return ApiResponse.ok(service.listAttachedCourseIds(discountId));
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PostMapping("/disable")
    public ApiResponse<Void> disable(@RequestParam Integer discountId) {
        service.disable(discountId);
        return ApiResponse.ok();
    }
}
