package com.microshop.elearningbackend.reporting.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.reporting.dto.*;
import com.microshop.elearningbackend.reporting.repository.ReportingDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final ReportingDao repo; // <--- đổi sang DAO

    @Transactional(readOnly = true)
    public RevenueSummaryResponse summarize(RevenueSummaryRequest req) {
        var r = normalize(req);
        var totals = repo.findTotals(r.start(), r.end(), r.courseId(), r.teacherId());
        var top    = repo.findTopCourses(r.start(), r.end(), r.teacherId(), r.courseId(), 5);
        var by     = (r.groupBy() == RevenueSummaryRequest.GroupBy.DAY)
                ? repo.findByDay(r.start(), r.end(), r.courseId(), r.teacherId())
                : repo.findByMonth(r.start(), r.end(), r.courseId(), r.teacherId());
        return new RevenueSummaryResponse(totals.revenue, totals.orders, top, by);
    }

    @Transactional(readOnly = true)
    public byte[] exportCsv(RevenueSummaryRequest req) {
        var r = normalize(req);
        var totals = repo.findTotals(r.start(), r.end(), r.courseId(), r.teacherId());
        var top    = repo.findTopCourses(r.start(), r.end(), r.teacherId(), r.courseId(), 10);
        var by     = (r.groupBy() == RevenueSummaryRequest.GroupBy.DAY)
                ? repo.findByDay(r.start(), r.end(), r.courseId(), r.teacherId())
                : repo.findByMonth(r.start(), r.end(), r.courseId(), r.teacherId());

        StringBuilder sb = new StringBuilder();
        sb.append("TotalRevenue,SuccessfulOrders\n")
                .append(totals.revenue).append(",").append(totals.orders).append("\n\n");

        sb.append("TopCourses (Revenue DESC)\n");
        sb.append("CourseId,CourseName,Revenue,Orders\n");
        for (var t : top) {
            sb.append(t.courseId()).append(",")
                    .append(csv(t.courseName())).append(",")
                    .append(t.revenue()).append(",")
                    .append(t.orders()).append("\n");
        }
        sb.append("\n");

        sb.append("By ").append(r.groupBy().name()).append("\n");
        sb.append("Period,Revenue,Orders\n");
        for (var p : by) {
            sb.append(p.period()).append(",")
                    .append(p.revenue()).append(",")
                    .append(p.orders()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] exportPdf(RevenueSummaryRequest req) {
        var r = normalize(req);
        var totals = repo.findTotals(r.start(), r.end(), r.courseId(), r.teacherId());
        var top    = repo.findTopCourses(r.start(), r.end(), r.teacherId(), r.courseId(), 10);
        var by     = (r.groupBy() == RevenueSummaryRequest.GroupBy.DAY)
                ? repo.findByDay(r.start(), r.end(), r.courseId(), r.teacherId())
                : repo.findByMonth(r.start(), r.end(), r.courseId(), r.teacherId());

        try {
            Document doc = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            var headFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            var cellFont  = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("Revenue Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph(
                    String.format("Range: %s to %s%s",
                            r.start(), r.end(),
                            r.courseId() != null ? " | CourseId=" + r.courseId() : ""
                    ), cellFont
            ));
            if (r.teacherId() != null) {
                doc.add(new Paragraph("TeacherId: " + r.teacherId(), cellFont));
            }
            doc.add(Chunk.NEWLINE);

            PdfPTable t1 = new PdfPTable(2);
            t1.setWidthPercentage(100);
            addHeader(t1, headFont, "TotalRevenue", "SuccessfulOrders");
            addRow(t1, cellFont, String.valueOf(totals.revenue), String.valueOf(totals.orders));
            doc.add(t1);
            doc.add(Chunk.NEWLINE);

            Paragraph hTop = new Paragraph("Top Courses (Revenue DESC)", headFont);
            doc.add(hTop);
            PdfPTable t2 = new PdfPTable(new float[]{15, 45, 20, 20});
            t2.setWidthPercentage(100);
            addHeader(t2, headFont, "CourseId", "CourseName", "Revenue", "Orders");
            for (var c : top) {
                addRow(t2, cellFont,
                        String.valueOf(c.courseId()), c.courseName(),
                        String.valueOf(c.revenue()), String.valueOf(c.orders()));
            }
            doc.add(t2);
            doc.add(Chunk.NEWLINE);

            Paragraph hBy = new Paragraph("By " + r.groupBy().name(), headFont);
            doc.add(hBy);
            PdfPTable t3 = new PdfPTable(new float[]{40, 30, 30});
            t3.setWidthPercentage(100);
            addHeader(t3, headFont, "Period", "Revenue", "Orders");
            for (var p : by) addRow(t3, cellFont, p.period(), String.valueOf(p.revenue()), String.valueOf(p.orders()));
            doc.add(t3);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("EXPORT_PDF_FAILED: " + e.getMessage());
        }
    }

    // ================= SRP helpers =================

    private record Normalized(LocalDateTime start, LocalDateTime end,
                              Integer courseId, Integer teacherId,
                              RevenueSummaryRequest.GroupBy groupBy) {}

    private Normalized normalize(RevenueSummaryRequest req) {
        LocalDateTime end   = (req.end()   == null) ? LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) : req.end();
        LocalDateTime start = (req.start() == null) ? end.minusDays(30) : req.start();
        if (!end.isAfter(start)) throw new ApiException("end must be after start");
        var groupBy = (req.groupBy() == null) ? RevenueSummaryRequest.GroupBy.DAY : req.groupBy();
        return new Normalized(start, end, req.courseId(), req.teacherId(), groupBy);
    }

    private static String csv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private static void addHeader(PdfPTable t, Font f, String... cols) {
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, f));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            t.addCell(cell);
        }
    }
    private static void addRow(PdfPTable t, Font f, String... cols) {
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, f));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            t.addCell(cell);
        }
    }
}
