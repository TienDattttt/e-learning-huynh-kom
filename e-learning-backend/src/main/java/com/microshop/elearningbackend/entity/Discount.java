package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Discount", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Discount_Code", columnNames = {"CodeDiscount"})
})
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DiscountId", nullable = false)
    private Integer id;

    @Column(name = "FromDate")
    private Instant fromDate;

    @Column(name = "ToDate")
    private Instant toDate;

    @Column(name = "CodeDiscount", nullable = false, length = 50)
    private String codeDiscount;

    @Column(name = "DiscountPercent")
    private Integer discountPercent;

    @Column(name = "DiscountAmount")
    private Long discountAmount;

}