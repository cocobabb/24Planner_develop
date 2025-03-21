package com.example.p24zip.domain.schedule.dto.response;

import com.example.p24zip.domain.schedule.entity.Schedule;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponseDto {

    private final Long id;
    private final String content;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String color;

    public static ScheduleResponseDto from(Schedule entity){
        return ScheduleResponseDto.builder()
            .id(entity.getId())
            .content(entity.getContent())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .color(entity.getColor())
            .build();
    }
}
