package com.radical.be_radicalcare.Utils;

import com.radical.be_radicalcare.Entities.User;
import com.radical.be_radicalcare.Services.JwtTokenProvider;
import com.radical.be_radicalcare.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public CustomOAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, ServletException, IOException {
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtTokenProvider.generateToken(authentication, user.getId(), null);

        // Lưu JWT vào Cookie
        Cookie jwtCookie = new Cookie("token", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 1 ngày
        response.addCookie(jwtCookie);

        // Redirect về Client
        response.sendRedirect("http://localhost:8081");
    }
}

