package com.example.p24zip.domain.house.dto.response;

import com.example.p24zip.domain.house.entity.House;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetHouseDetailsResponseDto {
    private final Long id;
    private final String nickname;
    private final String address1;
    private final String address2;
    private final String content;

    public static GetHouseDetailsResponseDto from(House house) {
        return GetHouseDetailsResponseDto.builder()
            .id(house.getId())
            .nickname(house.getNickname())
            .address1(house.getAddress1())
            .address2(house.getAddress2())
            .content(house.getContent())
            .build();
    }

}
