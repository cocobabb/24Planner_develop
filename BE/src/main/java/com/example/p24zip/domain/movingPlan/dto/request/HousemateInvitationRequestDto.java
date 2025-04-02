package com.example.p24zip.domain.movingPlan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HousemateInvitationRequestDto {

    @NotBlank
    private String code;

}
