package com.example.p24zip.domain.house.dto.response;

import com.example.p24zip.domain.house.entity.House;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeHouseDetailAddressResponseDto {
    private final Long id;
    private final String address2;

    public static ChangeHouseDetailAddressResponseDto from(House house) {
        return ChangeHouseDetailAddressResponseDto.builder()
            .id(house.getId())
            .address2(house.getAddress2())
            .build();
    }

}
