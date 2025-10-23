package com.microshop.elearningbackend.categories.controller;

import com.microshop.elearningbackend.categories.dto.CategoryNode;
import com.microshop.elearningbackend.categories.dto.SaveCategoryRequest;
import com.microshop.elearningbackend.categories.service.CategoryService;
import com.microshop.elearningbackend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    // Rule 3: chỉ GET/POST
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PostMapping("/save")
    public ApiResponse<Integer> save(@RequestBody SaveCategoryRequest req) {
        Integer id = service.saveUpsert(
                req.courseCategoryId(),
                req.name(),
                req.parentId(),
                req.sortOrder(),
                req.status()
        );
        return ApiResponse.ok(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tree")
    public ApiResponse<List<CategoryNode>> tree() {
        return ApiResponse.ok(service.getTree());
    }
}
