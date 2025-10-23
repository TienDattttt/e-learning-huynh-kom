package com.microshop.elearningbackend.auth.service;

import com.microshop.elearningbackend.entity.User;
import com.microshop.elearningbackend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepo;

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) return Optional.empty();

        // identity-service đặt subject = email
        String email = jwt.getSubject();
        if (email == null || email.isBlank()) return Optional.empty();

        return userRepo.findByEmail(email);
    }

    public Integer requireCurrentUserId() {
        return getCurrentUser().map(User::getId)
                .orElseThrow(() -> new RuntimeException("UNAUTHORIZED"));
    }

    public Collection<? extends GrantedAuthority> currentAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getAuthorities() : java.util.List.of();
    }
}
