package com.example.p24zip.domain.user.service;

import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import com.example.p24zip.domain.user.provider.OAuthUserInfo;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    private static final long TEMP_USER_EXPIRE_MINUTES = 10; // 10분 유지

    // 유저 정보 임시 저장
    public String saveTempUser(OAuthUserInfo oAuthInfo) {

        String tempToken = jwtTokenProvider.getTempToken(oAuthInfo);
        String key = oAuthInfo.getEmail();

        redisTemplate.opsForValue().set(key, tempToken, TEMP_USER_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return tempToken;
    }

    // 임시 저장된 유저 정보 조회
    public Map<String, String> getTempUser(String tempToken) {

        Map<String, String> tempUserInfoFromToken = jwtTokenProvider.getTempUserInfoFromToken(
            tempToken);
        String email = tempUserInfoFromToken.get("email");
        String value = redisTemplate.opsForValue().get(email);

            if (value == null || !value.equals(tempToken)) throw new IllegalArgumentException("유효하지 않은 토큰");

            return tempUserInfoFromToken;
    }

    // 임시 저장된 유저 정보 삭제
    public void deleteTempUser(String email) {
        redisTemplate.delete(email);
    }
}

