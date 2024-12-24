package com.radical.be_radicalcare.Utils;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        System.out.println("Request URI: " + requestURI);
        System.out.println("Token received: " + token);

        // Bỏ qua xác thực cho các endpoint public
        if (isPublicEndpoint(requestURI)) {
            System.out.println("Public endpoint accessed: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Xử lý token nếu có
        if (token != null) {
            System.out.println("Validating token: " + token);
            if (jwtTokenProvider.validateToken(token)) {
                System.out.println("Token is valid.");
                setAuthentication(token, request);
            } else {
                System.out.println("Token is invalid.");
            }
        } else {
            System.out.println("No token provided.");
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
            System.out.println("Token found in header: " + bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        // Lấy token từ cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    System.out.println("Token found in cookie: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        System.out.println("No token found in header or cookie.");
        return null; // Không tìm thấy token
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        try {
            String username = jwtTokenProvider.getUsernameFromJWT(token);
            List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRolesFromToken(token)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            System.out.println("Authentication details:");
            System.out.println("Username: " + username);
            System.out.println("Authorities: " + authorities);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Đặt authentication vào SecurityContextHolder
            if (authentication != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            System.out.println("Authentication set successfully.");
        } catch (Exception e) {
            System.out.println("Error setting authentication: " + e.getMessage());
            e.printStackTrace();
        }
    }
}