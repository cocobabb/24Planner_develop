package com.example.p24zip.global.security.jwt;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.provider.OAuthUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long accessTokenValidityInMilliseconds = 1000L * 60 * 30; // 30분
    private final long refreshTokenValidityInMilliseconds = 1000L * 60 * 60 * 48; // 48시간

    private final long invitationTokenValidityInMilliseconds =  1000L * 60 * 60 * 24;   // 24시간

    private static final long TEMP_USER_EXPIRATION_TIME = 10 * 60 * 1000;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String accessCreateToken(User user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("nickname", user.getNickname());
        claims.put("providerId", user.getProviderId());
        claims.put("provider", user.getProvider());

        Date now = new Date();

        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshCreateToken(User user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("nickname", user.getNickname());

        Date now = new Date();

        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getTempToken(OAuthUserInfo oAuthUserInfo) {
        Claims claims = Jwts.claims().setSubject(oAuthUserInfo.getEmail());
        claims.put("email", oAuthUserInfo.getEmail());
        claims.put("provider", oAuthUserInfo.getProvider());
        claims.put("providerId", oAuthUserInfo.getProviderId());

        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(new Date(System.currentTimeMillis() + TEMP_USER_EXPIRATION_TIME))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.get("email", String.class);
    }

    public Map<String, String> getTempUserInfoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

        String email = claims.get("email", String.class);
        String provider = claims.get("provider", String.class);
        String providerId = claims.get("providerId", String.class);

        // 데이터를 Map에 담아 반환
        Map<String, String> tempUserInfo = new HashMap<>();
        tempUserInfo.put("email", email);
        tempUserInfo.put("provider", provider);
        tempUserInfo.put("providerId", providerId);

        return tempUserInfo;
    }

    // 동거인 초대 링크를 위한 토큰 생성
    public String invitationToken(Long movingPlanId, User user) {

        Claims claims = Jwts.claims();
        claims.put("type", "invitation");
        claims.put("movingPlanId", movingPlanId);
        claims.put("inviterId", user.getId());

        Date now = new Date();

        Date validity = new Date(now.getTime() + invitationTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getMovingPlanId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("movingPlanId").toString()
        );
    }

    public Long getInviterId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("inviterId").toString()
        );
    }
}