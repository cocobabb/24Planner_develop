package com.example.p24zip.domain.user.controller;

import com.example.p24zip.domain.house.dto.response.ShowNicknameResponseDto;
import com.example.p24zip.domain.user.dto.request.ChangeNicknameRequestDto;
import com.example.p24zip.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.p24zip.domain.user.dto.response.ChangeNicknameResponseDto;
import com.example.p24zip.domain.user.dto.response.RedisValueResponseDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.service.AuthService;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final AuthService authService;


    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid ChangePasswordRequestDto requestDto,
        HttpServletResponse response ,@AuthenticationPrincipal User user) {
        authService.updatePassword(requestDto, response, user);

        return ResponseEntity.ok(
            ApiResponse.ok("UPDATED","비밀번호 수정에 성공했습니다.", null)
        );
    }

    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<ShowNicknameResponseDto>> getNickname(@AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "닉네임 조회에 성공했습니다.",
                authService.getNickname(user)
            )
        );
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<ChangeNicknameResponseDto>> updateNickname(@RequestBody @Valid ChangeNicknameRequestDto requestDto, @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.ok(
            "UPDATED",
            "닉네임 수정에 성공했습니다.",
            authService.updateNickname(requestDto, user)
        ));
    }

    @GetMapping("/redis/{key}")
    public ResponseEntity<ApiResponse<RedisValueResponseDto>> getRedisValue(@PathVariable String key){

        return ResponseEntity.ok(ApiResponse.ok(
            "OK",
            "redis 접근에 성공했습니다.",
            authService.getRedisValue(key)
        ));
    }
}
