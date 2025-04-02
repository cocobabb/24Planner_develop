package com.example.p24zip.domain.user.provider;

import com.example.p24zip.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final User user;
    private final OAuth2User oAuth2User;

    // 소셜로그인 유저 정보
    public CustomOAuth2User(OAuth2User oAuth2User, String nameAttributeKey, User user) {
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), nameAttributeKey);
        this.oAuth2User = oAuth2User;
        this.user = user;
    }
}
