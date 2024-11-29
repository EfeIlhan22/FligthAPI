package com.example.fligthapi.service;

import com.example.fligthapi.entity.Admin;
import com.example.fligthapi.repository.AdminRepository;
import com.example.fligthapi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final JwtUtils jwtUtils;
    private final AdminRepository adminRepository;

    @Autowired
    public AuthorizationService(JwtUtils jwtUtils, AdminRepository adminRepository) {
        this.jwtUtils = jwtUtils;
        this.adminRepository = adminRepository;
    }

    // JWT doğrulama ve kullanıcı adı alma
    public String getUsernameFromJwt(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getUsernameFromJwtToken(token);
        }
        throw new RuntimeException("Invalid JWT token");
    }

    // Kullanıcıyı JWT'ye göre doğrulama
    public Admin getAdminFromJwt(String token) {
        String username = getUsernameFromJwt(token);
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // Kullanıcıyı doğrulama ve yeni JWT oluşturma
    public String authenticateAndGenerateToken(String username, String password) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Here, you would also check the password (e.g., using bcrypt) before generating the token
        if (admin.getPassword().equals(password)) {
            // Generate token if authentication is successful
            return jwtUtils.generateTokenFromUsername(username);
        } else {
            throw new RuntimeException("Invalid password");
        }
    }

    // Kullanıcının yetkilerini kontrol etme (örn. Admin yetkisi)
    public boolean isAdmin(String token) {
        Admin admin = getAdminFromJwt(token);
        return admin.getRole().name().equals("ADMIN"); // Assuming role is stored as enum
    }

    // Authorization check: Ensures that the user has the correct role
    public boolean hasRole(String token, String requiredRole) {
        Admin admin = getAdminFromJwt(token);
        return admin.getRole().name().equalsIgnoreCase(requiredRole);
    }
}

