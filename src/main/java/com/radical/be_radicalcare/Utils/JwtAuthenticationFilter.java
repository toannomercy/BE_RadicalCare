package com.radical.be_radicalcare.Utils;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy token từ Header hoặc Cookie
        String token = getTokenFromHeaderOrCookie(request);
        String requestURI = request.getRequestURI();

        // Log thông tin request
        log.info("Request URI: {}", requestURI);
        log.info("Token received: {}", token);

        // Bỏ qua xác thực cho các endpoint public
        if (isPublicEndpoint(requestURI)) {
            log.info("Public endpoint accessed: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Xử lý token nếu có
        if (token != null) {
            log.info("Validating token: {}", token);
            if (jwtTokenProvider.validateToken(token)) {
                log.info("Token is valid.");
                setAuthentication(token, request);
            } else {
                log.warn("Token is invalid.");
            }
        } else {
            log.warn("No token provided.");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.equals("/api/v1/auth/register") ||
                requestURI.equals("/api/v1/auth/login") ||
                requestURI.equals("/api/v1/auth/forgot-password") ||
                requestURI.equals("/api/v1/login/oauth2/code/google");
    }

    private String getTokenFromHeaderOrCookie(HttpServletRequest request) {
        // Lấy token từ header Authorization
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.debug("Token found in header: {}", bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        // Lấy token từ cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.debug("Cookie name: {}, value: {}", cookie.getName(), cookie.getValue());
                if ("token".equals(cookie.getName())) {
                    log.debug("Token found in cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        log.warn("No token found in header or cookie.");
        return null; // Không tìm thấy token
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        try {
            String username = jwtTokenProvider.getUsernameFromJWT(token);
            List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRolesFromToken(token)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            log.info("Authentication details:");
            log.info("Username: {}", username);
            log.info("Authorities: {}", authorities);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Đặt authentication vào SecurityContextHolder
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            log.info("Authentication set successfully.");
        } catch (Exception e) {
            log.error("Error setting authentication: {}", e.getMessage(), e);
        }
    }
}