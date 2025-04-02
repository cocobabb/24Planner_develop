package com.example.p24zip.domain.user.service;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.AdditionalInfoRequiredException;
import com.example.p24zip.domain.user.provider.CustomOAuth2User;
import com.example.p24zip.domain.user.provider.KakaoOAuthUserInfo;
import com.example.p24zip.domain.user.provider.OAuthUserInfo;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final TempUserService tempUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 사용자 정보 (attributes) 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth 제공자 ID 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth 제공자에 맞는 사용자 정보 가져오기
        OAuthUserInfo oAuthUserInfo = getOAuthUserInfo(registrationId, attributes);

        // 이메일로 사용자 조회
        String email = oAuthUserInfo.getEmail();
        User user = userRepository.findByUsername(email).orElseThrow(() -> {
            String tempToken = tempUserService.saveTempUser(oAuthUserInfo);
            return new AdditionalInfoRequiredException(
                "INFO_REQUIRED",
                "닉네임 입력 후 회원가입할 수 있습니다.",
                tempToken
            );
        });

        // 동일한 메일로 소셜로그인을 시도한 경우
        String provider = user.getProvider();
        if (provider == null || !provider.equals(registrationId)) {
            throw new OAuth2AuthenticationException("기존에 회원가입한 방식으로 로그인 후 이용 가능합니다.");
        }

        // OAuth 제공자에서 제공하는 사용자 이름 속성 값 가져오기
        String nameAttributeKey = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        return new CustomOAuth2User(oAuth2User, nameAttributeKey, user);
    }

    // 등록된 OAuth 제공자에 맞는 사용자 정보를 반환
    private OAuthUserInfo getOAuthUserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "kakao":
                return new KakaoOAuthUserInfo(attributes);
//            case "google":
//                return new GoogleOAuthUserInfo(attributes);
//            case "naver":
//                return new NaverOAuthUserInfo(attributes);
            default:
                throw new OAuth2AuthenticationException("지원되지 않는 OAuth 제공자입니다.");
        }
    }
}
