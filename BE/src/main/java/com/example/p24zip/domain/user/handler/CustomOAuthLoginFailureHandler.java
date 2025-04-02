package com.example.p24zip.domain.user.handler;

import com.example.p24zip.global.exception.AdditionalInfoRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuthLoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${origin}")
    private String origin;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        if (exception instanceof AdditionalInfoRequiredException ex) {

            // 임시 토큰을 파라미터로 추가하여, 추가 정보 입력 페이지로 리다이렉트
            String tempToken = ex.getTempToken();
            response.sendRedirect(origin + "/signup/additional-info?code=" + tempToken);
            return;
        }

        response.sendRedirect(origin + "/login-redirect");
    }
}
