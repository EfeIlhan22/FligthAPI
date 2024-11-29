package com.example.fligthapi.controller;
import com.example.fligthapi.dto.AdminCreateDTO;
import com.example.fligthapi.entity.Admin;
import com.example.fligthapi.entity.Role;
import com.example.fligthapi.repository.AdminRepository;
import com.example.fligthapi.security.JwtUtils;
import com.example.fligthapi.security.UserDetailsImpl;
import com.example.fligthapi.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthorizationService authorizationService;
    private final AdminRepository adminRepository;
    // Kullanıcı girişi ve JWT oluşturma
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            // Kullanıcıyı doğrula ve token'ı oluştur
            String token = authorizationService.authenticateAndGenerateToken(username, password);

            // Token'ı bir HTTP-only cookie olarak gönder
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(token);

            // Kullanıcıyı başarıyla yanıtla
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())  // Cookie'yi Set-Cookie başlığına ekle
                    .body(Map.of("message", "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // JWT doğrulama ve kullanıcı adı alma
    @GetMapping("/username")
    public ResponseEntity<?> getUsernameFromJwt(@RequestHeader("Authorization") String token) {
        try {
            // Bearer başlığını çıkar
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = authorizationService.getUsernameFromJwt(tokenWithoutBearer);
            return ResponseEntity.ok(username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }
    }

    // Kullanıcının Admin rolüne sahip olup olmadığını kontrol etme
    @GetMapping("/isAdmin")
    public ResponseEntity<?> isAdmin(@RequestHeader("Authorization") String token) {
        try {
            // Bearer başlığını çıkar
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
            boolean isAdmin = authorizationService.isAdmin(tokenWithoutBearer);
            return ResponseEntity.ok(isAdmin ? "User is Admin" : "User is not Admin");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }
    }

    // Kullanıcının belirli bir rolü olup olmadığını kontrol etme
    @GetMapping("/hasRole")
    public ResponseEntity<?> hasRole(@RequestHeader("Authorization") String token, @RequestParam String role) {
        try {
            // Bearer başlığını çıkar
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
            boolean hasRole = authorizationService.hasRole(tokenWithoutBearer, role);
            return ResponseEntity.ok(hasRole ? "User has role: " + role : "User does not have role: " + role);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody AdminCreateDTO adminCreateDTO) {
        try {
            // Role kontrolü

            // Yeni Admin objesi oluşturuluyor
            Admin newAdmin = new Admin();
            newAdmin.setUsername(adminCreateDTO.getUsername());
            newAdmin.setPassword(adminCreateDTO.getPassword());  // Şifreyi hash'lemedik

            log.info("Attempting to save admin: {}", newAdmin.getUsername()); // Admin kaydetme öncesi log

            // Admin veritabanına kaydediliyor
            adminRepository.save(newAdmin);

            // Başarı mesajı
            log.info("Admin created successfully: {}", newAdmin.getUsername());  // Başarı log
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin created successfully with username: " + newAdmin.getUsername());
        } catch (Exception e) {
            log.error("Error creating admin: {}", e.getMessage(), e);  // Hata mesajı log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating admin: " + e.getMessage());
        }
    }

}
