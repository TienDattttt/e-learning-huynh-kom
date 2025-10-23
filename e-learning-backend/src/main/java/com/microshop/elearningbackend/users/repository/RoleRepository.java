package com.microshop.elearningbackend.users.repository;

import com.microshop.elearningbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName); // ví dụ: "HocVien", "GiangVien", "Admin"
}
