package com.radical.be_radicalcare.Services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key jwtSecret;

    public JwtTokenProvider() {
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Khóa bí mật để ký JWT
    }

    // Tạo JWT với quyền hạn (roles)
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList(); // Lấy danh sách quyền từ Authentication

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 604800000); // 7 ngày

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities) // Gắn quyền vào payload của token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret) // Ký token bằng secret key
                .compact();
    }

    // Xác thực JWT
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Token JWT không hợp lệ.");
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT đã hết hạn.");
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT không được hỗ trợ.");
        } catch (IllegalArgumentException ex) {
            log.error("Chuỗi claims của token JWT trống.");
        }
        return false;
    }

    // Lấy username từ JWT
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Lấy danh sách quyền hạn (roles) từ JWT
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("authorities", List.class);
    }
}
