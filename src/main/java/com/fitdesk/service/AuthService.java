package com.fitdesk.service;

import com.fitdesk.entity.AdminUser;
import com.fitdesk.repository.AdminUserRepository;
import com.fitdesk.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String email, String password) {
        AdminUser user = adminUserRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return jwtUtil.generateToken(user);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
