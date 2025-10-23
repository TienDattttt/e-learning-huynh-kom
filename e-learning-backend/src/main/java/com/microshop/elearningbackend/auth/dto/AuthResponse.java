// src/main/java/.../auth/dto/AuthResponse.java
package com.microshop.elearningbackend.auth.dto;

public record AuthResponse(String accessToken, long expiresIn, String scope) {}
