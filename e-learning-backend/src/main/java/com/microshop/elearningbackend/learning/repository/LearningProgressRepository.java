package com.microshop.elearningbackend.learning.repository;

import com.microshop.elearningbackend.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    // --- Các hàm cũ ---
    Optional<LearningProgress> findByUser_IdAndLesson_Id(Integer userId, Integer lessonId);

    List<LearningProgress> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    @Query("""
           select coalesce(avg(lp.progressPercent), 0)
           from LearningProgress lp
           where lp.user.id = :userId and lp.course.id = :courseId
           """)
    Double avgProgressByCourse(Integer userId, Integer courseId);


    // --- Các hàm MỚI để giảng viên quản lý học viên ---

    // Tổng hợp tiến độ trung bình & mốc cập nhật mới nhất cho nhiều học viên trong 1 khóa
    @Query("""
        select lp.user.id as userId,
               avg(lp.progressPercent) as avgPercent,
               max(lp.updatedAt) as lastUpdated
        from LearningProgress lp
        where lp.course.id = :courseId and lp.user.id in :studentIds
        group by lp.user.id
    """)
    List<Object[]> aggregateCourseProgress(Integer courseId, List<Integer> studentIds);


    // Chi tiết tiến độ từng bài học của 1 học viên trong 1 khóa
    @Query("""
        select lp.lesson.id, lp.lesson.name, lp.progressPercent, lp.isCompleted, lp.updatedAt
        from LearningProgress lp
        where lp.course.id = :courseId and lp.user.id = :userId
        order by lp.lesson.id asc
    """)
    List<Object[]> findLessonProgress(Integer courseId, Integer userId);
    @Query("SELECT COUNT(DISTINCT lp.user.id) FROM LearningProgress lp WHERE lp.course.id = :courseId")
    long countDistinctUsersByCourseId(Integer courseId);
}
