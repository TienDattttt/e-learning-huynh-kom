package com.microshop.elearningbackend.reporting.repository;

import com.microshop.elearningbackend.reporting.dto.PeriodRevenueDto;
import com.microshop.elearningbackend.reporting.dto.TopCourseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportingDao {

    @PersistenceContext
    private EntityManager em;

    public static final class Totals {
        public final long revenue;
        public final long orders;
        public Totals(long revenue, long orders) {
            this.revenue = revenue; this.orders = orders;
        }
    }

    public Totals findTotals(LocalDateTime start, LocalDateTime end, Integer courseId, Integer teacherId) {
        String sql = """
            SELECT 
                COALESCE(SUM(od.TotalAmount), 0) AS revenue,
                COUNT(DISTINCT o.OrderId)        AS orders
            FROM dbo.Orders o
            JOIN dbo.OrderDetails od ON od.OrderId = o.OrderId
            JOIN dbo.Courses c       ON c.CourseId = od.CourseId
            WHERE o.Status = 'SUCCESS'
              AND o.OrderDate >= ?1 AND o.OrderDate < ?2
              AND (?3 IS NULL OR c.CourseId = ?3)
              AND (?4 IS NULL OR c.UsersId = ?4)
        """;
        Query q = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, courseId)
                .setParameter(4, teacherId);

        Object[] row = (Object[]) q.getSingleResult();
        long revenue = toLong(row[0]);
        long orders  = toLong(row[1]);
        return new Totals(revenue, orders);
    }

    public List<TopCourseDto> findTopCourses(LocalDateTime start, LocalDateTime end,
                                             Integer teacherId, Integer courseId, int limit) {
        // Sanitize limit (SQL literal, KHÔNG bind trong FETCH NEXT)
        int top = Math.max(1, Math.min(limit, 100));

        String sql = """
            SELECT 
                c.CourseId,
                c.Name,
                COALESCE(SUM(od.TotalAmount), 0) AS revenue,
                COUNT(DISTINCT o.OrderId)        AS orders
            FROM dbo.Orders o
            JOIN dbo.OrderDetails od ON od.OrderId = o.OrderId
            JOIN dbo.Courses c       ON c.CourseId = od.CourseId
            WHERE o.Status = 'SUCCESS'
              AND o.OrderDate >= ?1 AND o.OrderDate < ?2
              AND (?3 IS NULL OR c.UsersId = ?3)
              AND (?4 IS NULL OR c.CourseId = ?4)
            GROUP BY c.CourseId, c.Name
            ORDER BY revenue DESC
            OFFSET 0 ROWS FETCH NEXT %d ROWS ONLY
        """.formatted(top);

        Query q = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, teacherId)
                .setParameter(4, courseId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        List<TopCourseDto> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            Integer cId   = toInt(r[0]);
            String  cName = (String) r[1];
            long revenue  = toLong(r[2]);
            long orders   = toLong(r[3]);
            list.add(new TopCourseDto(cId, cName, revenue, orders));
        }
        return list;
    }

    public List<PeriodRevenueDto> findByDay(LocalDateTime start, LocalDateTime end,
                                            Integer courseId, Integer teacherId) {
        String sql = """
            SELECT 
                CONVERT(char(10), CAST(o.OrderDate AS date), 23) AS period, -- yyyy-MM-dd
                COALESCE(SUM(od.TotalAmount), 0) AS revenue,
                COUNT(DISTINCT o.OrderId)        AS orders
            FROM dbo.Orders o
            JOIN dbo.OrderDetails od ON od.OrderId = o.OrderId
            JOIN dbo.Courses c       ON c.CourseId = od.CourseId
            WHERE o.Status = 'SUCCESS'
              AND o.OrderDate >= ?1 AND o.OrderDate < ?2
              AND (?3 IS NULL OR c.CourseId = ?3)
              AND (?4 IS NULL OR c.UsersId = ?4)
            GROUP BY CAST(o.OrderDate AS date)
            ORDER BY period ASC
        """;
        Query q = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, courseId)
                .setParameter(4, teacherId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        List<PeriodRevenueDto> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String period = (String) r[0];
            long revenue  = toLong(r[1]);
            long orders   = toLong(r[2]);
            list.add(new PeriodRevenueDto(period, revenue, orders));
        }
        return list;
    }

    public List<PeriodRevenueDto> findByMonth(LocalDateTime start, LocalDateTime end,
                                              Integer courseId, Integer teacherId) {
        String sql = """
            SELECT 
                FORMAT(o.OrderDate, 'yyyy-MM')   AS period,
                COALESCE(SUM(od.TotalAmount), 0) AS revenue,
                COUNT(DISTINCT o.OrderId)        AS orders
            FROM dbo.Orders o
            JOIN dbo.OrderDetails od ON od.OrderId = o.OrderId
            JOIN dbo.Courses c       ON c.CourseId = od.CourseId
            WHERE o.Status = 'SUCCESS'
              AND o.OrderDate >= ?1 AND o.OrderDate < ?2
              AND (?3 IS NULL OR c.CourseId = ?3)
              AND (?4 IS NULL OR c.UsersId = ?4)
            GROUP BY FORMAT(o.OrderDate, 'yyyy-MM')
            ORDER BY period ASC
        """;
        Query q = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, courseId)
                .setParameter(4, teacherId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        List<PeriodRevenueDto> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String period = (String) r[0];
            long revenue  = toLong(r[1]);
            long orders   = toLong(r[2]);
            list.add(new PeriodRevenueDto(period, revenue, orders));
        }
        return list;
    }

    // ------- helpers -------
    private static long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }
}
