package com.example.p24zip.domain.schedule.dto.request;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.schedule.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleRequestDto {

    @NotBlank
    private String content;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    private String color;

    public Schedule toEntity(MovingPlan movingPlan){
        return Schedule.builder()
            .content(this.content)
            .startDate(this.startDate)
            .endDate(this.endDate)
            .color(this.color)
            .movingPlan(movingPlan)
            .build();
    }

}
