package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HousemateInvitationAcceptResponseDto {

    private Long id;
    private Long movingPlanId;

    public static HousemateInvitationAcceptResponseDto from(Housemate housemate) {
        return HousemateInvitationAcceptResponseDto.builder()
                .id(housemate.getId())
                .movingPlanId(housemate.getMovingPlan().getId())
                .build();
    }
}
