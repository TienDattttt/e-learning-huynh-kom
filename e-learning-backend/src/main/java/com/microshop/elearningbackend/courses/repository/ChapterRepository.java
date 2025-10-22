package com.microshop.elearningbackend.courses.repository;

import com.microshop.elearningbackend.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByCourse_IdOrderByOrderChapterAsc(Integer courseId);
}
