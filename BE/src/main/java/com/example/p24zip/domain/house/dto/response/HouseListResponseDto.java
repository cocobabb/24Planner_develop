package com.example.p24zip.domain.house.dto.response;


import com.example.p24zip.domain.house.entity.House;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HouseListResponseDto {
    List<HouseResponseDto> houses;


    public static HouseListResponseDto from(List<House> houseList) {
        return HouseListResponseDto.builder()
            .houses(
                houseList.stream()
                    .map(HouseResponseDto::from)
                    .sorted(Comparator.comparing(HouseResponseDto::getUpdatedAt).reversed())  // updatedAt 기준 내림차순 정렬
                    .toList()
            )
            .build();
    }

    @Getter
    @Builder
    static class HouseResponseDto {
        private final Long id;
        private final String nickname;
        private final double latitude;
        private final double longitude;
        private final LocalDateTime updatedAt;

        public static HouseResponseDto from(House house){
            return HouseResponseDto.builder()
                .id(house.getId())
                .nickname(house.getNickname())
                .latitude(house.getLatitude())
                .longitude(house.getLongitude())
                .updatedAt(house.getUpdatedAt())
                .build();
        }
    }



}
