package com.microshop.elearningbackend.reporting.service;

import com.microshop.elearningbackend.auth.service.CurrentUserService;
import com.microshop.elearningbackend.reporting.dto.*;
import com.microshop.elearningbackend.reporting.repository.DashboardDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardDao dao;
    private final CurrentUserService currentUser;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Integer year, Integer month, Integer quarter, Integer courseId) {
        // ✅ Xác định teacherId nếu là giảng viên
        Integer teacherId = null;
        boolean isTeacher = currentUser.currentAuthorities()
                .stream().anyMatch(a -> "ROLE_GiangVien".equalsIgnoreCase(a.getAuthority()));
        if (isTeacher) {
            teacherId = currentUser.requireCurrentUserId();
        }

        // ✅ Gọi 3 SP nhỏ
        var summary = dao.getSummary(teacherId, courseId, year, month, quarter);
        var trend = dao.getRevenueTrend(teacherId, courseId, year);
        var topCourses = dao.getTopCourses(teacherId, year);

        return new DashboardResponse(summary, trend, topCourses);
    }
}
