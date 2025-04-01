package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.Housemate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HousemateResponseDto {

    private Long id;
    private String username;
    private String nickname;
    private Boolean isOwner;

    public static HousemateResponseDto from(Housemate housemate) {
        return HousemateResponseDto.builder()
                .id(housemate.getId())
                .username(housemate.getUser().getUsername())
                .nickname(housemate.getUser().getNickname())
                .isOwner(housemate.getIsOwner())
                .build();
    }
}
