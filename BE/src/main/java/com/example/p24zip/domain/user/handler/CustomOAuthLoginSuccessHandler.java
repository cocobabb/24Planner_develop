package com.example.p24zip.domain.user.handler;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import com.example.p24zip.domain.user.provider.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class CustomOAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${origin}")
    private String origin;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();
        String nickname = user.getNickname();

        // 토큰 생성
        String refreshToken = jwtTokenProvider.refreshCreateToken(user);
        String accessToken = jwtTokenProvider.accessCreateToken(user);

        // 쿠키 생성 및 refreshToken 쿠키에 넣기
        Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(17800);
            cookie.setPath("/");
            response.addCookie(cookie);

        // refreshToken redis 넣기
        redisTemplate.opsForValue().set(refreshToken, refreshToken, 2, TimeUnit.DAYS);

        // 로그인 리다이렉트 페이지로 이동
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(origin + "/login-redirect")
            .queryParam("nickname", nickname)
            .queryParam("code", accessToken)
            .toUriString();

        response.sendRedirect(redirectUrl);

    }
}
