package com.example.p24zip.domain.movingPlan.dto.request;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MovingPlanRequestDto {

    @NotBlank
    private String title;

    public MovingPlan toEntity(User user) {
        return MovingPlan.builder()
                .title(this.title)
                .user(user)
                .build();
    }
}
