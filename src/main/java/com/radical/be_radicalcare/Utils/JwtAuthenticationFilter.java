package com.radical.be_radicalcare.Utils;

import com.radical.be_radicalcare.Services.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization");

        // Kiểm tra xem request có phải là đăng ký hoặc đăng nhập không, nếu có thì bỏ qua việc kiểm tra token
        if (request.getRequestURI().equals("/api/v1/auth/register") || request.getRequestURI().equals("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);  // Tiến hành filter mà không kiểm tra token
            return;
        }

        // Nếu không phải đăng ký hoặc đăng nhập, tiến hành kiểm tra token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer " khỏi token

            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromJWT(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);  // Thêm các authorities nếu cần
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Tiến hành filter tiếp
        filterChain.doFilter(request, response);
    }
}

