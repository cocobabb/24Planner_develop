package com.example.p24zip.domain.house.dto.response;

import com.example.p24zip.domain.house.entity.House;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeHouseContentResponseDto {
    private final Long id;
    private final String content;

    public static ChangeHouseContentResponseDto from(House house) {
        return ChangeHouseContentResponseDto.builder()
            .id(house.getId())
            .content(house.getContent())
            .build();
    }
}
