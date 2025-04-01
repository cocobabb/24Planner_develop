package com.example.p24zip.domain.user.dto.response;

import com.example.p24zip.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeNicknameResponseDto {

    private final Long id;
    private final String nickname;

}
