package com.microshop.elearningbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Roles", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__Roles__8A2B6160CF1EBD3D", columnNames = {"RoleName"})
})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId", nullable = false)
    private Integer id;

    @Column(name = "RoleName", nullable = false, length = 50)
    private String roleName;

}