// src/main/java/.../auth/config/SecurityConfig.java
package com.microshop.elearningbackend.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Đọc theo cả hai tên: ưu tiên app.security.jwt.signer-key, fallback jwt.signerKey
    @Value("${app.security.jwt.signer-key:${jwt.signerKey}}")
    private String signerKey;

    @Value("${app.security.jwt.valid-duration:${jwt.valid-duration:3600}}")
    private long accessTokenSeconds;

    @Value("${app.security.jwt.refreshable-duration:${jwt.refreshable-duration:36000}}")
    private long refreshableSeconds;

    @Value("${app.security.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    @Value("${app.security.cors.allowed-methods:GET,POST}")
    private List<String> allowedMethods;

    @Value("${app.security.cors.allowed-headers:Authorization,Content-Type}")
    private List<String> allowedHeaders;

    private SecretKey secretKey() {
        return new SecretKeySpec(signerKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        // PUBLIC
                        .requestMatchers(
                                "/actuator/health",
                                "/api/courses/public/**",
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/me"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/files/**", "/images/**").permitAll()
                        // còn lại cần token
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthConverter())
                        )
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":false,\"message\":\"UNAUTHORIZED\",\"data\":null}");
                        })
                );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withSecretKey(secretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Encoder để KÝ token
    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** scope chứa ROLE_Admin / ROLE_GiangVien / ROLE_HocVien ... */
    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        var c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(jwt -> {
            String scope = Optional.ofNullable(jwt.getClaimAsString("scope"))
                    .orElse(jwt.getClaimAsString("scp"));
            if (scope == null || scope.isBlank()) return List.of();
            return Arrays.stream(scope.split("\\s+"))
                    .filter(s -> !s.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return c;
    }

    @Bean
    CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(allowedOrigins);
        cfg.setAllowedMethods(allowedMethods);
        cfg.setAllowedHeaders(allowedHeaders);
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
