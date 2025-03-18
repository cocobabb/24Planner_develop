package com.example.p24zip.domain.user.repository;

import com.example.p24zip.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByUsername(String userName);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsername(String username);

}
