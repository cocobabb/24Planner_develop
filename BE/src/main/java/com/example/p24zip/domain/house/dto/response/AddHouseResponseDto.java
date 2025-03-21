package com.example.p24zip.domain.house.dto.response;

import com.example.p24zip.domain.house.entity.House;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddHouseResponseDto {
    private final Long id;
    private final String nickname;
    private final String address1;
    private final String address2;
    private final double longitude;
    private final double latitude;

    public static AddHouseResponseDto from (House house){
        return AddHouseResponseDto.builder()
            .id(house.getId())
            .nickname(house.getNickname())
            .address1(house.getAddress1())
            .address2(house.getAddress2())
            .longitude(house.getLongitude())
            .latitude(house.getLatitude())
            .build();
    }
}
