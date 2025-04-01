package com.example.p24zip.domain.movingPlan.dto.request;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class MovingPlanRequestDto {

    @NotBlank
    @Length(max = 20)
    private String title;

    public MovingPlan toEntity() {
        return MovingPlan.builder()
                .title(this.title)
                .build();
    }
}
