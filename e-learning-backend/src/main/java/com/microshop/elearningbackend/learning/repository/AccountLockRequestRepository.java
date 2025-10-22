package com.microshop.elearningbackend.learning.repository;

import com.microshop.elearningbackend.entity.AccountLockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountLockRequestRepository extends JpaRepository<AccountLockRequest, Long> {

    // Kiểm tra xem có request PENDING cho (teacher, student) chưa
    @Query("""
        select case when count(r)>0 then true else false end
        from AccountLockRequest r
        where r.teacher.id = :teacherId
          and r.student.id = :studentId
          and r.status = 'PENDING'
    """)
    boolean existsPending(Integer teacherId, Integer studentId);

    // Lấy request PENDING hiện tại (để update lý do hoặc timestamp)
    Optional<AccountLockRequest> findFirstByTeacher_IdAndStudent_IdAndStatus(
            Integer teacherId, Integer studentId, String status
    );

    // Danh sách request PENDING của 1 giảng viên
    @Query("""
        select r
        from AccountLockRequest r
        where r.teacher.id = :teacherId
          and r.status = 'PENDING'
        order by r.createdAt desc
    """)
    List<AccountLockRequest> findPendingByTeacher(Integer teacherId);
}
