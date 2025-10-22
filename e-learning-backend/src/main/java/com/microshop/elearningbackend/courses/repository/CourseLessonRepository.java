package com.microshop.elearningbackend.courses.repository;

import com.microshop.elearningbackend.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CourseLessonRepository extends JpaRepository<CourseLesson, Integer> {
    List<CourseLesson> findByChapter_IdInOrderBySortOrderAsc(Collection<Integer> chapterIds);
    List<CourseLesson> findByChapter_IdOrderBySortOrderAsc(Integer chapterId);
}
