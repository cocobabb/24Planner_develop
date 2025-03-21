package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovingPlanResponseDto {
    private Long id;
    private String title;

    public static MovingPlanResponseDto from(MovingPlan movingPlan) {
        return MovingPlanResponseDto.builder()
                .id(movingPlan.getId())
                .title(movingPlan.getTitle())
                .build();
    }
}
