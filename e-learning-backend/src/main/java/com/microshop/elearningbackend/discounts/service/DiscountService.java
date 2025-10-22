package com.microshop.elearningbackend.discounts.service;

import com.microshop.elearningbackend.common.ApiPage;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import com.microshop.elearningbackend.discounts.dto.AttachCoursesRequest;
import com.microshop.elearningbackend.discounts.dto.DetachCoursesRequest;
import com.microshop.elearningbackend.discounts.dto.DiscountDto;
import com.microshop.elearningbackend.discounts.dto.SaveDiscountRequest;
import com.microshop.elearningbackend.discounts.repository.DiscountCourseRepository;
import com.microshop.elearningbackend.discounts.repository.DiscountRepository;
import com.microshop.elearningbackend.entity.Cours;
import com.microshop.elearningbackend.entity.Discount;
import com.microshop.elearningbackend.entity.DiscountCourse;
import com.microshop.elearningbackend.entity.DiscountCourseId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepo;
    private final DiscountCourseRepository dcRepo;
    private final CourseRepository courseRepo;

    /* ======================
       PUBLIC API (GET/POST)
       ====================== */

    @Transactional
    public DiscountDto save(SaveDiscountRequest req) {
        validateSave(req);
        Discount d = upsertDiscount(req);
        return toDto(d);
    }

    @Transactional(readOnly = true)
    public ApiPage<DiscountDto> list(String q, Boolean activeOnly, int page, int size) {
        Page<Discount> p = discountRepo.findAll(PageRequest.of(page, size)); // đơn giản hóa, có thể tự viết spec/filter
        List<DiscountDto> items = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Discount d : p.getContent()) {
            boolean active = isActive(d, now);
            if (activeOnly != null && activeOnly && !active) continue;
            if (q != null && !q.isBlank()) {
                if (!d.getCodeDiscount().toLowerCase().contains(q.toLowerCase())) continue;
            }
            items.add(toDto(d));
        }
        return new ApiPage<>(items, p.getNumber(), p.getSize(), p.getTotalElements());
    }

    @Transactional
    public void attachCourses(AttachCoursesRequest req) {
        Discount d = requireDiscount(req.discountId());

        for (Integer courseId : req.courseIds()) {
            Cours c = requireCourse(courseId);

            if (dcRepo.existsLink(d.getId(), c.getId())) continue;

            DiscountCourse dc = new DiscountCourse();

            // 1) Tạo và GÁN SẴN 2 KHÓA vào EmbeddedId
            DiscountCourseId embeddedId = new DiscountCourseId();
            embeddedId.setDiscountId(d.getId());
            embeddedId.setCourseId(c.getId());
            dc.setId(embeddedId);

            // 2) Set đủ 2 quan hệ @MapsId để Hibernate sync nhất quán
            dc.setDiscount(d);
            dc.setCourse(c);

            dcRepo.save(dc);
        }
    }


    @Transactional
    public void detachCourses(DetachCoursesRequest req) {
        Discount d = requireDiscount(req.discountId());
        for (Integer courseId : req.courseIds()) {
            dcRepo.deleteLink(d.getId(), courseId);
        }
    }

    @Transactional(readOnly = true)
    public List<Integer> listAttachedCourseIds(Integer discountId) {
        requireDiscount(discountId);
        return dcRepo.findAllByDiscountId(discountId)
                .stream()
                .map(dc -> dc.getCourse().getId())  // ưu tiên lấy từ quan hệ
                .toList();
    }

    @Transactional
    public void disable(Integer discountId) {
        Discount d = requireDiscount(discountId);
        // Không có cột status -> set ToDate = now - 1s
        d.setToDate(LocalDateTime.now().minusSeconds(1));
        discountRepo.save(d);
    }

    /* ======================
       PRIVATE HELPERS (SRP)
       ====================== */

    private void validateSave(SaveDiscountRequest req) {
        if (req.code() == null || req.code().isBlank()) throw new ApiException("code is required");
        boolean hasPercent = req.percent() != null && req.percent() > 0;
        boolean hasAmount  = req.amount() != null && req.amount() > 0;
        if (hasPercent == hasAmount) throw new ApiException("Either percent or amount must be provided (XOR)");
        if (hasPercent && (req.percent() < 1 || req.percent() > 100)) {
            throw new ApiException("percent must be 1..100");
        }
        // code unique: nếu create hoặc đổi code -> check
        if (req.discountId() == null || codeChanged(req)) {
            discountRepo.findByCodeDiscount(req.code()).ifPresent(x -> {
                throw new ApiException("Voucher code already exists: " + req.code());
            });
        }
        if (req.fromDate() != null && req.toDate() != null && req.fromDate().isAfter(req.toDate())) {
            throw new ApiException("fromDate must be before toDate");
        }
    }

    private boolean codeChanged(SaveDiscountRequest req) {
        if (req.discountId() == null) return true;
        return discountRepo.findById(req.discountId())
                .map(x -> !x.getCodeDiscount().equalsIgnoreCase(req.code()))
                .orElse(true);
    }

    private Discount upsertDiscount(SaveDiscountRequest req) {
        Discount d = (req.discountId() == null)
                ? new Discount()
                : discountRepo.findById(req.discountId())
                .orElseThrow(() -> new ApiException("Discount not found: " + req.discountId()));

        d.setCodeDiscount(req.code());
        d.setDiscountPercent(req.percent());
        d.setDiscountAmount(req.amount());
        d.setFromDate(req.fromDate());
        d.setToDate(req.toDate());
        return discountRepo.save(d);
    }

    private Discount requireDiscount(Integer id) {
        return discountRepo.findById(id)
                .orElseThrow(() -> new ApiException("Discount not found: id=" + id));
    }

    private Cours requireCourse(Integer id) {
        return courseRepo.findById(id)
                .orElseThrow(() -> new ApiException("Course not found: id=" + id));
    }

    private DiscountDto toDto(Discount d) {
        return new DiscountDto(
                d.getId(),
                d.getCodeDiscount(),
                d.getDiscountPercent(),
                d.getDiscountAmount(),
                d.getFromDate(),
                d.getToDate(),
                isActive(d, LocalDateTime.now())
        );
    }

    private boolean isActive(Discount d, LocalDateTime now) {
        var from = d.getFromDate();
        var to   = d.getToDate();
        if (from != null && now.isBefore(from)) return false;
        if (to != null && now.isAfter(to)) return false;
        return true;
    }
}
