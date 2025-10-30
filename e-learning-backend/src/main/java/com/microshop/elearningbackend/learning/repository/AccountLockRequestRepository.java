package com.microshop.elearningbackend.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.microshop.elearningbackend.entity.AccountLockRequest;

import java.util.List;
import java.util.Map;

@Repository
public interface AccountLockRequestRepository extends JpaRepository<AccountLockRequest, Long> {

    // SP 1: Tạo yêu cầu khóa
    @Query(value = "EXEC sp_lock_request_create :teacherId, :studentId, :reason", nativeQuery = true)
    Map<String, Object> createLockRequest(
            @Param("teacherId") int teacherId,
            @Param("studentId") int studentId,
            @Param("reason") String reason);

    // SP 2: Danh sách yêu cầu chờ xử lý
    @Query(value = "EXEC sp_lock_request_pending_list", nativeQuery = true)
    List<Map<String, Object>> findPendingRequests();

    // SP 3: Cập nhật trạng thái
    @Query(value = "EXEC sp_lock_request_update_status :requestId, :status", nativeQuery = true)
    Map<String, Object> updateStatus(
            @Param("requestId") long requestId,
            @Param("status") String status);
}
