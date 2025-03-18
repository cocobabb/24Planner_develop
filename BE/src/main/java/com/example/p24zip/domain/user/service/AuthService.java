package com.example.p24zip.domain.user.service;


import com.example.p24zip.domain.user.dto.request.LoginRequestDto;
import com.example.p24zip.domain.user.dto.request.SignupRequestDto;
import com.example.p24zip.domain.user.dto.response.AccessTokenResponseDto;
import com.example.p24zip.domain.user.dto.response.LoginResponseDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import com.example.p24zip.global.exception.TokenException;
import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void signup(@Valid SignupRequestDto requestDto) {
        boolean checkUsername = checkExistsUsername(requestDto.getUsername());

        if (checkUsername) {
            throw new CustomException("EXIST_EMAIL", "이미 사용중인 이메일입니다.");
        }

        User user = requestDto.toEntity();
        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()
                )
        );

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException());

        // 토큰 생성
        String accessjwt = jwtTokenProvider.accessCreateToken(user);
        String refreshjwt = jwtTokenProvider.refreshCreateToken(user);

        // 쿠키 생성 및 refreshToken 쿠키에 넣기
        Cookie cookie = new Cookie("refreshToken",refreshjwt);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        // refreshToken redis 넣기
        redisTemplate.opsForValue().set("refreshToken", refreshjwt, 2, TimeUnit.DAYS);

        return new LoginResponseDto(accessjwt, refreshjwt);
    }

    // refresh token 검증 및 access token 재발급
    public AccessTokenResponseDto reissue(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        // cookie에서 refresh 추출
        String refresh = findByRefreshToken(cookies);
        if(refresh == null || !jwtTokenProvider.validateToken(refresh)) {
            throw new TokenException();
        }


        String refreshusername = jwtTokenProvider.getUsername(refresh);
        if(refreshusername == null) {
            throw new TokenException();
        }

        String redistoken = (String) redisTemplate.opsForValue().get("refreshToken");

        User user = userRepository.findByUsername(refreshusername)
                .orElseThrow(() -> new ResourceNotFoundException());

        String accessjwt = null;

        if(refresh.equals(redistoken)) {
            accessjwt = jwtTokenProvider.accessCreateToken(user);
        } else {
            throw new TokenException();
        }

        return new AccessTokenResponseDto(accessjwt);
    }

    // 로그아웃
    public void logout(HttpServletResponse response) {

        // redis에서 RefreshToken 삭제
        redisTemplate.delete("refreshToken");

        // 쿠키에서 RefreshToken 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String findByRefreshToken(Cookie[] cookies) {
        String refresh = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("refreshToken")) {
                    refresh = cookie.getValue();
                    return refresh;
                }
            }
        }
        return refresh;
    }

    public boolean checkExistsUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }

    public void checkExistNickname(String nickname) {
        boolean checkExistNickname = userRepository.existsByNickname(nickname);

        if(checkExistNickname) {
           throw new CustomException("EXIST_NICKNAME","이미 사용중인 닉네임입니다.");
        }
    }


}
