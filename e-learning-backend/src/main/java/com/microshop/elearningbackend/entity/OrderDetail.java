package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "OrderDetails", schema = "dbo", indexes = {
        @Index(name = "IX_OrderDetails_OrderId", columnList = "OrderId"),
        @Index(name = "IX_OrderDetails_CourseId", columnList = "CourseId"),
        @Index(name = "IX_OrderDetails_DiscountId", columnList = "DiscountId")
})
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderDetailId", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "OrderId", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourseId", nullable = false)
    private Cours course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DiscountId")
    private Discount discount;

    @Column(name = "Amount")
    private Long amount;

    @Column(name = "TotalAmount")
    private Long totalAmount;

}