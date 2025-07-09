package com.example.p24zip.domain.movingPlan.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HousemateNotificationDto {

    private String newHousemateUsername;
    private String movingPlanTitle;
    private List<String> existingHousemateUsernames;

    @Builder
    public HousemateNotificationDto(String newHousemateUsername,
        String movingPlanTitle,
        List<String> existingHousemateUsernames) {
        this.newHousemateUsername = newHousemateUsername;
        this.movingPlanTitle = movingPlanTitle;
        this.existingHousemateUsernames = existingHousemateUsernames;
    }

} 