package com.example.p24zip.global.notification.fcm;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;


    // client가 FCM server로 Fcm token 생성 요청
    @PostMapping("/createToken")
    public ApiResponse<CustomCode> createToken(@RequestBody FcmCreateTokenRequestDto requestDTO,
        @AuthenticationPrincipal User user)
        throws IOException {

        fcmService.saveFcmDeviceToken(requestDTO, user);
        return ApiResponse.ok(CustomCode.FCM_TOKEN_CREATE);
    }

    // fcm 서버로 메세지 요청(부하테스트용)
    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody FcmMessage fcmMessage) throws IOException {
        fcmService.sendMessageTo(
            fcmMessage.getMessage().getToken(),
            fcmMessage.getMessage().getNotification().getTitle(),
            fcmMessage.getMessage().getNotification().getBody()
        );
    }

}