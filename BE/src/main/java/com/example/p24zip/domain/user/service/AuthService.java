package com.example.p24zip.domain.user.service;


import com.example.p24zip.domain.user.dto.request.SignupRequestDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(@Valid SignupRequestDto requestDto) {
        boolean checkUsername = checkExistsUsername(requestDto.getEmail());

        if (checkUsername) {
            throw new CustomException("EXIST_EMAIL", "이미 사용중인 이메일입니다.");
        }

        User user = requestDto.toEntity();
        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    public boolean checkExistsUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }
}
