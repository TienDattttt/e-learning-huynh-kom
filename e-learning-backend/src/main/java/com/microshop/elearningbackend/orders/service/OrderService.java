package com.microshop.elearningbackend.orders.service;

import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.courses.repository.CourseRepository;
import com.microshop.elearningbackend.discounts.repository.DiscountCourseRepository;
import com.microshop.elearningbackend.discounts.repository.DiscountRepository;
import com.microshop.elearningbackend.entity.Cours;
import com.microshop.elearningbackend.entity.Discount;
import com.microshop.elearningbackend.entity.Order;
import com.microshop.elearningbackend.entity.OrderDetail;
import com.microshop.elearningbackend.entity.User;
import com.microshop.elearningbackend.orders.dto.BuyCourseRequest;
import com.microshop.elearningbackend.orders.dto.BuyCourseResponse;
import com.microshop.elearningbackend.orders.dto.MyCourseDto;
import com.microshop.elearningbackend.orders.repository.OrderDetailRepository;
import com.microshop.elearningbackend.orders.repository.OrderRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderDetailRepository orderDetailRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final DiscountRepository discountRepo;
    private final DiscountCourseRepository discountCourseRepo;

    /* =============================
       PUBLIC API (Controller calls)
       ============================= */

    @Transactional
    public BuyCourseResponse buy(BuyCourseRequest req) {
        validateBuyInput(req);

        User user = requireUser(req.userId());
        Cours course = requirePublishedCourse(req.courseId());

        // Đã mua chưa?
        if (orderDetailRepo.existsPurchasedCourse(user.getId(), course.getId())) {
            return new BuyCourseResponse(null, "ALREADY_OWNED", 0L);
        }

        // Tính giá (áp dụng promotionPrice, voucher nếu có và hợp lệ)
        long baseAmount = calcBaseAmount(course);
        Discount voucher = (req.voucherCode() != null && !req.voucherCode().isBlank())
                ? resolveValidVoucher(req.voucherCode())
                : null;
        long finalAmount = applyDiscountForCourse(baseAmount, voucher, course.getId());

        // Lưu Order + OrderDetail (auto thanh toán thành công)
        Order order = createOrder(user, req.payMethod(), finalAmount);
        createOrderDetail(order, course, voucher, baseAmount, finalAmount);

        return new BuyCourseResponse(order.getId(), "SUCCESS", finalAmount);
    }

    @Transactional(readOnly = true)
    public List<MyCourseDto> myCourses(Integer userId) {
        if (userId == null) throw new ApiException("userId is required");

        List<OrderDetail> purchased = orderDetailRepo.findPurchasedDetailsByUser(userId);

        List<MyCourseDto> result = new ArrayList<>();
        for (OrderDetail d : purchased) {
            var o = d.getOrder();
            var c = d.getCourse();
            result.add(new MyCourseDto(
                    c.getId(),
                    c.getName(),
                    c.getImage(),
                    (c.getUsers() != null ? c.getUsers().getId() : null),
                    o.getOrderDate()   // LocalDateTime
            ));
        }
        return result;
    }

    /* =============================
       PRIVATE HELPERS (Decomposition)
       ============================= */

    private void validateBuyInput(BuyCourseRequest req) {
        if (req.userId() == null) throw new ApiException("userId is required");
        if (req.courseId() == null) throw new ApiException("courseId is required");
        // payMethod tạm optional; mặc định "AUTO"
    }

    private User requireUser(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ApiException("User not found: id=" + id));
    }

    private Cours requirePublishedCourse(Integer courseId) {
        Cours c = courseRepo.findById(courseId)
                .orElseThrow(() -> new ApiException("Course not found: id=" + courseId));
        if (!Boolean.TRUE.equals(c.getStatus())) {
            throw new ApiException("Course is not published");
        }
        return c;
    }

    private long calcBaseAmount(Cours c) {
        Long p = c.getPromotionPrice();
        Long base = (p != null && p > 0) ? p : c.getPrice();
        if (base == null) base = 0L;
        return base;
    }

    private Discount resolveValidVoucher(String code) {
        Discount d = discountRepo.findByCodeDiscount(code)
                .orElseThrow(() -> new ApiException("Voucher not found: " + code));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = d.getFromDate();
        LocalDateTime to = d.getToDate();
        if ((from != null && now.isBefore(from)) || (to != null && now.isAfter(to))) {
            throw new ApiException("Voucher expired or not active");
        }
        // hợp lệ
        return d;
    }

    private long applyDiscountForCourse(long baseAmount, Discount voucher, Integer courseId) {
        if (voucher == null) return baseAmount;

        // Nếu voucher có gắn ít nhất 1 course, thì chỉ áp dụng cho những course nằm trong mapping
        boolean hasMapping = discountCourseRepo.existsAnyCourseLinked(voucher.getId());
        if (hasMapping && !discountCourseRepo.existsLink(voucher.getId(), courseId)) {
            return baseAmount; // không thuộc mapping -> không giảm
        }

        // Áp dụng giảm giá
        if (voucher.getDiscountPercent() != null) {
            long off = Math.round(baseAmount * (voucher.getDiscountPercent() / 100.0));
            return Math.max(baseAmount - off, 0L);
        }
        if (voucher.getDiscountAmount() != null) {
            return Math.max(baseAmount - voucher.getDiscountAmount(), 0L);
        }
        return baseAmount;
    }

    private String generateOrderCode() {
        // Ví dụ: ORD-20251022-220559-3F7A1C8B
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0,8).toUpperCase();
        return "ORD-" + ts + "-" + rand;
    }


    private Order createOrder(User user, String payMethod, long finalAmount) {
        Order o = new Order();

        // TẠO MÃ ĐƠN HÀNG DUY NHẤT
        o.setOrderCode(generateOrderCode());

        o.setUsers(user);
        o.setPayMethod((payMethod == null || payMethod.isBlank()) ? "AUTO" : payMethod);
        o.setOrderDate(LocalDateTime.now());
        o.setStatus("SUCCESS"); // auto thành công (tích hợp cổng sau)
        o.setTotalAmount(finalAmount);
        return orderRepo.save(o);
    }

    private void createOrderDetail(Order order, Cours course, Discount voucher,
                                   long amount, long totalAmount) {
        OrderDetail d = new OrderDetail();
        d.setOrder(order);
        d.setCourse(course);
        d.setDiscount(voucher);
        d.setAmount(amount);
        d.setTotalAmount(totalAmount);
        orderDetailRepo.save(d);
        // KHÔNG add vào order.getOrderDetails() vì entity Order không có collection
    }

    private LocalDateTime toLocalDateTime(LocalDateTime dt) {
        return dt; // entity Order.orderDate của bạn là LocalDateTime (theo schema)
    }
}
