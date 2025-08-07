package com.example.p24zip.domain.user.repository;

import com.example.p24zip.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String userName);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);
}
