package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.Housemate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovingPlanOwnerResponseDto {

    private Long id;
    private String title;
    private Boolean isOwner;

    public static MovingPlanOwnerResponseDto from(Housemate housemate) {
        return MovingPlanOwnerResponseDto.builder()
                .id(housemate.getMovingPlan().getId())
                .title(housemate.getMovingPlan().getTitle())
                .isOwner(housemate.getIsOwner())
                .build();
    }
}
