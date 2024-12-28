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

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestURI = request.getRequestURI();
//        String token = request.getHeader("Authorization");
//
//
//        if (requestURI.equals("/api/v1/auth/register") ||
//                requestURI.equals("/api/v1/auth/login") ||
//                requestURI.equals("/api/v1/auth/forgot-password") ||
//                requestURI.equals("/api/v1/login/oauth2/code/google")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//
//            if (jwtTokenProvider.validateToken(token)) {
//                String username = jwtTokenProvider.getUsernameFromJWT(token);
//
//                String userId = jwtTokenProvider.getUserIdFromJWT(token);
//
//                // Lấy authorities từ token
//                List<String> roles = jwtTokenProvider.getRolesFromToken(token);
//
//                List<SimpleGrantedAuthority> authorities = roles.stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//                // Tạo đối tượng xác thực
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(username, null, authorities);
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        String token = getTokenFromHeaderOrCookie(request); // Lấy token từ header hoặc cookie
        System.out.println("Token in JwtAuthenticationFilter: " + token);
        String requestURI = request.getRequestURI();

    // Bỏ qua xác thực cho các endpoint public
    if (requestURI.equals("/api/v1/auth/register") ||
            requestURI.equals("/api/v1/auth/login") ||
            requestURI.equals("/api/v1/auth/forgot-password") ||
            requestURI.equals("/api/v1/login/oauth2/code/google")) {
        filterChain.doFilter(request, response);
        return;
    }

    // Xử lý token nếu có
        if (token != null && jwtTokenProvider.validateToken(token)) {
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        // Tạo đối tượng xác thực
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
            setAuthentication(token, request);
        }

        filterChain.doFilter(request, response);
    }

    // Hàm lấy token từ header hoặc cookie
    private String getTokenFromHeaderOrCookie(HttpServletRequest request) {
        // Lấy token từ header Authorization
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Lấy token từ cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null; // Không tìm thấy token
    }
    // Thiết lập SecurityContextHolder
    private void setAuthentication(String token, HttpServletRequest request) {
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRolesFromToken(token)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
