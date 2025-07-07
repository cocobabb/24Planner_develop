package com.example.p24zip.domain.movingPlan.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class HousemateNotificationDto {
    private String newHousemateName; // 새로 추가된 사용자 이름
    private String movingPlanTitle; // MovingPlan 제목
    private List<String> existingHousemateUsernames; // 기존 Housemate 사용자명 목록

    @Builder
    public HousemateNotificationDto(String newHousemateName, 
                                    String movingPlanTitle, 
                                    List<String> existingHousemateUsernames) {
        this.newHousemateName = newHousemateName;
        this.movingPlanTitle = movingPlanTitle;
        this.existingHousemateUsernames = existingHousemateUsernames;
    }

    // RedisNotificationDto로 변환
    public RedisNotificationDto toRedisNotificationDto(String username) {
        return RedisNotificationDto.builder()
                .username(username)
                .type("newHousemate")
                .message(String.format("%s님이 \"%s\" 플랜에 참여했습니다.", 
                        newHousemateName, movingPlanTitle))
                .build();
    }
} 