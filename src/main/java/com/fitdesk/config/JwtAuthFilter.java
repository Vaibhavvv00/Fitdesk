package com.fitdesk.config;

import com.fitdesk.entity.AdminUser;
import com.fitdesk.repository.AdminUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminUserRepository adminUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    AdminUser user = adminUserRepository.findByEmail(email).orElse(null);
                    
                    if (user != null && jwtUtil.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log exception here if needed, continue filter chain
        }
        
        filterChain.doFilter(request, response);
    }
}
