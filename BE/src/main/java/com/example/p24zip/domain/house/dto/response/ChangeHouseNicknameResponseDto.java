package com.example.p24zip.domain.house.dto.response;

import com.example.p24zip.domain.house.entity.House;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeHouseNicknameResponseDto {
    private final Long id;
    private final String nickname;

    public static ChangeHouseNicknameResponseDto from(House house) {
        return ChangeHouseNicknameResponseDto.builder()
            .id(house.getId())
            .nickname(house.getNickname())
            .build();
    }
}
