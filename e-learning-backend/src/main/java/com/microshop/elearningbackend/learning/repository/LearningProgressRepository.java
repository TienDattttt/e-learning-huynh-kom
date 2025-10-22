package com.microshop.elearningbackend.learning.repository;

import com.microshop.elearningbackend.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    Optional<LearningProgress> findByUser_IdAndLesson_Id(Integer userId, Integer lessonId);

    List<LearningProgress> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    @Query("""
           select coalesce(avg(lp.progressPercent), 0)
           from LearningProgress lp
           where lp.user.id = :userId and lp.course.id = :courseId
           """)
    Double avgProgressByCourse(Integer userId, Integer courseId);
}
