// src/main/java/.../auth/dto/LoginResponse.java
package com.microshop.elearningbackend.auth.dto;

public record LoginResponse(
        String accessToken,
        long   expiresIn,
        String tokenType,
        String scope,
        Integer userId,
        String email,
        String fullName
) {}
