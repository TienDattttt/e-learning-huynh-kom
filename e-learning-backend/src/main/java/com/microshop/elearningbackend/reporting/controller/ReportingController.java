package com.microshop.elearningbackend.reporting.controller;

import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.reporting.dto.RevenueSummaryRequest;
import com.microshop.elearningbackend.reporting.dto.RevenueSummaryResponse;
import com.microshop.elearningbackend.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/revenue")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService service;

    @PreAuthorize("hasAnyAuthority('ROLE_Admin','ROLE_GiangVien')")
    @PostMapping("/summary")
    public ApiResponse<RevenueSummaryResponse> summary(@RequestBody RevenueSummaryRequest req) {
        return ApiResponse.ok(service.summarize(req));
    }

    // Export CSV
    @PreAuthorize("hasAnyAuthority('ROLE_Admin','ROLE_GiangVien')")
    @PostMapping(value = "/export.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestBody RevenueSummaryRequest req) {
        byte[] data = service.exportCsv(req);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"revenue.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    // Export PDF
    @PreAuthorize("hasAnyAuthority('ROLE_Admin','ROLE_GiangVien')")
    @PostMapping(value = "/export.pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody RevenueSummaryRequest req) {
        byte[] data = service.exportPdf(req);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"revenue.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
