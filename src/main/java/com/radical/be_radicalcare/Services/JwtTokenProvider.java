package com.radical.be_radicalcare.Services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key signingKey;  // Khóa mã hóa JWT

    // Constructor: Khởi tạo jwtSecret và signingKey
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        // Chuỗi secret key lấy từ application.properties
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // Lấy tên người dùng từ authentication
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList(); // Lấy danh sách quyền hạn (roles)

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 604800000); // Token expires in 7 days

        log.info("Generating JWT for username: {}", username);

        return Jwts.builder()
                .setSubject(username) // Gắn username vào token
                .claim("authorities", authorities) // Gắn danh sách quyền hạn vào token
                .setIssuedAt(now) // Thời gian phát hành
                .setExpiration(expiryDate) // Thời gian hết hạn
                .signWith(signingKey) // Ký token bằng khóa signingKey
                .compact();
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(authToken);
            log.info("JWT token is valid.");
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT token claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Error while validating JWT: {}", ex.getMessage());
        }
        return false;
    }


    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Trả về username từ token
    }


    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("authorities", List.class); // Trả về danh sách roles
    }
}
