package com.microshop.elearningbackend.reporting.repository;

import com.microshop.elearningbackend.reporting.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DashboardDao {

    @PersistenceContext
    private EntityManager em;

    public DashboardSummaryDto getSummary(Integer teacherId, Integer courseId, Integer year, Integer month, Integer quarter) {
        Query q = em.createNativeQuery("EXEC sp_dashboard_summary ?, ?, ?, ?, ?");
        q.setParameter(1, teacherId);
        q.setParameter(2, courseId);
        q.setParameter(3, year);
        q.setParameter(4, month);
        q.setParameter(5, quarter);

        Object[] r = (Object[]) q.getSingleResult();
        long revenue = toLong(r[0]);
        long orders  = toLong(r[1]);
        return new DashboardSummaryDto(revenue, orders);
    }

    public List<RevenueTrendDto> getRevenueTrend(Integer teacherId, Integer courseId, Integer year) {
        Query q = em.createNativeQuery("EXEC sp_dashboard_revenue_trend ?, ?, ?");
        q.setParameter(1, teacherId);
        q.setParameter(2, courseId);
        q.setParameter(3, year);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new RevenueTrendDto(toInt(r[0]), toLong(r[1])))
                .toList();
    }

    public List<TopCourseDto> getTopCourses(Integer teacherId, Integer year) {
        Query q = em.createNativeQuery("EXEC sp_dashboard_top_courses ?, ?");
        q.setParameter(1, teacherId);
        q.setParameter(2, year);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream()
                .map(r -> new TopCourseDto(toInt(r[0]), (String) r[1], toLong(r[2]), toLong(r[3])))
                .toList();
    }

    // ===== helpers =====
    private static long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
    private static int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }
}
