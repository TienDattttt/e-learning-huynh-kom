package com.microshop.elearningbackend.auth.controller;

import com.microshop.elearningbackend.auth.dto.AuthResponse;
import com.microshop.elearningbackend.auth.dto.LoginRequest;
import com.microshop.elearningbackend.auth.dto.RegisterRequest;
import com.microshop.elearningbackend.auth.service.AuthService;
import com.microshop.elearningbackend.auth.service.CurrentUserService;
import com.microshop.elearningbackend.common.ApiResponse;
import com.microshop.elearningbackend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService current;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ApiResponse.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req));
    }

    // public để FE kiểm tra token/hiển thị user
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        var opt = current.getCurrentUser();
        return opt.<ApiResponse<Map<String, Object>>>map(user ->
                ApiResponse.ok(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName(),
                        "role", user.getRole() != null ? user.getRole().getRoleName() : null,
                        "authorities", current.currentAuthorities().stream().map(Object::toString).toList()
                ))
        ).orElseGet(() -> ApiResponse.fail("UNAUTHORIZED"));
    }
}
