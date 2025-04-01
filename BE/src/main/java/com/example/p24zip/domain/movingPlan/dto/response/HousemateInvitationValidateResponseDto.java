package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HousemateInvitationValidateResponseDto {

    private Long movingPlanId;
    private String planTitle;
    private String inviterName;

    public static HousemateInvitationValidateResponseDto from(MovingPlan movingPlan, User user) {
        return HousemateInvitationValidateResponseDto.builder()
                .movingPlanId(movingPlan.getId())
                .planTitle(movingPlan.getTitle())
                .inviterName(user.getNickname())
                .build();
    }
}
