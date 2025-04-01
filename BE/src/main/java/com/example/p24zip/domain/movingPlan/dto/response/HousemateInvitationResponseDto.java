package com.example.p24zip.domain.movingPlan.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HousemateInvitationResponseDto {

    private final String invitationLink;

    public static HousemateInvitationResponseDto from(String invitationLink) {
        return HousemateInvitationResponseDto.builder()
                .invitationLink(invitationLink)
                .build();
    }
}
