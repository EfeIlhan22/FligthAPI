package com.example.fligthapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JwtUtils {
    private final String jwtSecret="2440304a41233d7e483b6271622d74647277575b226a40227363456f4d";

    private int jwtExpirationMs=360000;

    private String jwtCookieName="jwtCookie";

    public Optional<String> getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {  // Cookie adı "token" olarak belirledik
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public ResponseCookie generateJwtCookie(String token) {
        return ResponseCookie.from("token", token)  // Cookie ismi ve token değeri
                .httpOnly(true)  // JavaScript tarafından erişilemez
                .secure(true)    // Yalnızca HTTPS üzerinden gönderilsin
                .path("/")       // Cookie tüm alan adı için geçerli
                .maxAge(Duration.ofHours(1))  // 1 saat geçerlilik süresi
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public String generateTokenFromUsername(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", List.of("ADMIN")) // Rolleri otomatik olarak "ADMIN" ayarla
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
        }
        return false;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
