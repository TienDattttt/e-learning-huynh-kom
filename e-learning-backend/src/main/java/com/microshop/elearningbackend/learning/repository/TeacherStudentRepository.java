package com.microshop.elearningbackend.learning.repository;

import com.microshop.elearningbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeacherStudentRepository extends JpaRepository<User, Integer> {

    // Lấy distinct student đã mua khóa của teacher (có thể filter courseId)
    @Query("""
        select distinct u
        from OrderDetail od
        join od.order o
        join od.course c
        join o.users u
        where c.users.id = :teacherId
          and (:courseId is null or c.id = :courseId)
    """)
    Page<User> findStudentsOfTeacher(Integer teacherId, Integer courseId, Pageable pageable);

    // Kiểm tra student có thực sự thuộc teacher theo course hay không
    @Query("""
        select case when count(od) > 0 then true else false end
        from OrderDetail od
        where od.course.users.id = :teacherId
          and od.order.users.id = :studentId
          and (:courseId is null or od.course.id = :courseId)
    """)
    boolean studentBelongsToTeacher(Integer teacherId, Integer studentId, Integer courseId);
}
