package com.example.fligthapi.repository;

import com.example.fligthapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

     Optional<Admin> findByUsername(String username);
}
