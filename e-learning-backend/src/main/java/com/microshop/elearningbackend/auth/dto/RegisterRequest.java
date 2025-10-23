// src/main/java/.../auth/dto/RegisterRequest.java
package com.microshop.elearningbackend.auth.dto;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        String role   // "HocVien" | "GiangVien" | "Admin"
) {}
