package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "AccountLockRequests", schema = "dbo", indexes = {
        @Index(name = "IX_LockReq_Teacher", columnList = "TeacherId"),
        @Index(name = "IX_LockReq_Student", columnList = "StudentId")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ_LockReq_UniquePending", columnNames = {"TeacherId", "StudentId", "Status"})
})
public class AccountLockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestId", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TeacherId", nullable = false)
    private User teacher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "StudentId", nullable = false)
    private User student;

    @Size(max = 1000)
    @Nationalized
    @Column(name = "Reason", length = 1000)
    private String reason;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'PENDING'")
    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

}