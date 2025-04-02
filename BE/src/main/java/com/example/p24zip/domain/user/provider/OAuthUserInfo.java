package com.example.p24zip.domain.user.provider;

public interface OAuthUserInfo {

    // 소셜로그인 제공자 (ex. 카카오, 구글, 네이버)
    String getProvider();

    // 소셜로그인에서의 사용자 ID
    String getProviderId();

    // 유저의 이메일 정보
    String getEmail();

}
