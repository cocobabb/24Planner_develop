package com.example.p24zip.domain.user.controller;

import com.example.p24zip.domain.house.dto.response.ShowNicknameResponseDto;
import com.example.p24zip.domain.user.dto.request.ChangeNicknameRequestDto;
import com.example.p24zip.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.p24zip.domain.user.dto.response.ChangeNicknameResponseDto;
import com.example.p24zip.domain.user.dto.response.RedisValueResponseDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.service.AuthService;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<ApiResponse<Void>> updatePassword(
        @RequestBody @Valid ChangePasswordRequestDto requestDto,
        HttpServletResponse response, @AuthenticationPrincipal User user) {
        authService.updatePassword(requestDto, response, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.PASSWORD_RESET_SUCCESS.getCode(),
                CustomCode.PASSWORD_RESET_SUCCESS.getMessage(),
                null
            )
        );
    }

    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<ShowNicknameResponseDto>> getNickname(
        @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.USER_NICKNAME_LOAD_SUCCESS.getCode(),
                CustomCode.USER_NICKNAME_LOAD_SUCCESS.getMessage(),
                authService.getNickname(user)
            )
        );
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<ChangeNicknameResponseDto>> updateNickname(
        @RequestBody @Valid ChangeNicknameRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.USER_NICKNAME_UPDATE_SUCCESS.getCode(),
                CustomCode.USER_NICKNAME_UPDATE_SUCCESS.getMessage(),
                authService.updateNickname(requestDto, user)
            )
        );
    }

    @GetMapping("/redis/{key}")
    public ResponseEntity<ApiResponse<RedisValueResponseDto>> getRedisValue(
        @PathVariable String key) {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.USER_REDIS_ACCESS_SUCCESS.getCode(),
                CustomCode.USER_REDIS_ACCESS_SUCCESS.getMessage(),
                authService.getRedisValue(key)
            )
        );
    }

    @DeleteMapping("/delete")
    public void deleteUser(@AuthenticationPrincipal User user) {
        authService.deleteUser(user);
    }
}
