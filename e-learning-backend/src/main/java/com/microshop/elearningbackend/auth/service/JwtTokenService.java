// src/main/java/.../auth/service/JwtTokenService.java
package com.microshop.elearningbackend.auth.service;

import com.microshop.elearningbackend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtEncoder encoder;

    @Value("${app.security.jwt.valid-duration:${jwt.valid-duration:3600}}")
    private long accessTokenSeconds;

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenSeconds);

        String scope = roleToScope(user); // VD: "ROLE_Admin" hoặc "ROLE_GiangVien"

        var claims = JwtClaimsSet.builder()
                .issuer("elearning")          // tuỳ chọn
                .issuedAt(now)
                .expiresAt(exp)
                .subject(user.getEmail())     // subject = email (giống identity-service)
                .claim("uid", user.getId())
                .claim("fullName", user.getFullName())
                .claim("scope", scope)
                .id(UUID.randomUUID().toString())
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String roleToScope(User u) {
        if (u.getRole() == null || u.getRole().getRoleName() == null) return "";
        String rn = u.getRole().getRoleName();
        // Chuẩn hoá theo hệ thống hiện dùng:
        return switch (rn) {
            case "Admin", "ADMIN" -> "ROLE_Admin";
            case "GiangVien", "TEACHER" -> "ROLE_GiangVien";
            case "HocVien", "STUDENT" -> "ROLE_HocVien";
            default -> "ROLE_HocVien"; // mặc định
        };
    }
}
