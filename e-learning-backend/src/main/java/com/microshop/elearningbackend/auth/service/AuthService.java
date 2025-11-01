package com.microshop.elearningbackend.auth.service;

import com.microshop.elearningbackend.auth.dto.AuthResponse;
import com.microshop.elearningbackend.auth.dto.LoginRequest;
import com.microshop.elearningbackend.auth.dto.RegisterRequest;
import com.microshop.elearningbackend.common.exception.ApiException;
import com.microshop.elearningbackend.entity.Role;
import com.microshop.elearningbackend.entity.User;
import com.microshop.elearningbackend.users.repository.RoleRepository;
import com.microshop.elearningbackend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.security.jwt.valid-duration:${jwt.valid-duration:3600}}")
    private long accessTokenSeconds;

    public AuthResponse register(RegisterRequest req) {
        if (req.email() == null || req.email().isBlank()) throw new ApiException("email is required");
        if (req.password() == null || req.password().isBlank()) throw new ApiException("password is required");
        if (userRepo.findByEmail(req.email()).isPresent()) throw new ApiException("Email already exists");

        Role role = roleRepo.findByRoleName(req.role() == null ? "HocVien" : req.role())
                .orElseThrow(() -> new ApiException("Role not found: " + req.role()));

        User u = new User();
        u.setFullName(req.fullName() == null ? "No Name" : req.fullName());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(role);
        userRepo.save(u);

        return issueToken(u);
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new ApiException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            throw new ApiException("Invalid email or password");
        }
        return issueToken(u);
    }

    private AuthResponse issueToken(User u) {
        String scope = toScope(u); // ví dụ: "ROLE_HocVien" hoặc "ROLE_GiangVien"

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("elearning")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenSeconds, ChronoUnit.SECONDS))
                .subject(u.getEmail())                 // subject = email
                .claim("uid", u.getId())
                .claim("fullName", u.getFullName())
                .claim("scope", scope)                 // chứa ROLE_*
                .build();

        JwsHeader jws = JwsHeader.with(() -> "HS256").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(jws, claims)).getTokenValue();

        return new AuthResponse(token, accessTokenSeconds, scope);
    }

    private String toScope(User u) {
        // map Role.roleName ("HocVien"/"GiangVien"/"Admin") -> "ROLE_HocVien"...
        String roleName = (u.getRole() != null ? u.getRole().getRoleName() : "HocVien");
        return "ROLE_" + roleName;
    }
}
