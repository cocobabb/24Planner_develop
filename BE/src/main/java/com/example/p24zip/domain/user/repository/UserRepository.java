package com.example.p24zip.domain.user.repository;

import com.example.p24zip.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByUsername(String userName);
}
