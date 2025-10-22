package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Users", schema = "dbo", indexes = {
        @Index(name = "IX_Users_RoleId", columnList = "RoleId")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Users_Email", columnNames = {"Email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleId", nullable = false)
    private Role role;

    @Nationalized
    @Column(name = "FullName", nullable = false, length = 200)
    private String fullName;

    @Column(name = "Sex")
    private Boolean sex;

    @Column(name = "Birthday")
    private Instant birthday;

    @Column(name = "Email", nullable = false, length = 300)
    private String email;

    @Column(name = "Password", nullable = false, length = 300)
    private String password;

    @Column(name = "Token", length = 2000)
    private String token;

    @Column(name = "ImageUrl", length = 2000)
    private String imageUrl;

}