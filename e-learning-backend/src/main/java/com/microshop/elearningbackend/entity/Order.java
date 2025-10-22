package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Orders", schema = "dbo", indexes = {
        @Index(name = "IX_Orders_UsersId", columnList = "UsersId")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ__Orders__999B5229EE881440", columnNames = {"OrderCode"})
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderId", nullable = false)
    private Long id;

    @Column(name = "OrderCode", length = 50)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UsersId", nullable = false)
    private User users;

    @Column(name = "PayMethod", length = 50)
    private String payMethod;

    @ColumnDefault("getdate()")
    @Column(name = "OrderDate", nullable = false)
    private Instant orderDate;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "TotalAmount")
    private Long totalAmount;

}