package com.microshop.elearningbackend.categories.repository;

import com.microshop.elearningbackend.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer> {
    List<CourseCategory> findAllByStatusTrueOrderBySortOrderAsc();
}
