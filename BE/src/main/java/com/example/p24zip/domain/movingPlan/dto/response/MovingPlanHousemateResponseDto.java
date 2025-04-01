package com.example.p24zip.domain.movingPlan.dto.response;

import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MovingPlanHousemateResponseDto {

    private Long id;
    private String title;
    private Long housemateId;
    private Boolean isOwner;
    private List<HousemateResponseDto> housemates;

    public static MovingPlanHousemateResponseDto from(MovingPlan movingPlan, Housemate housemate, List<Housemate> housemates) {
        return MovingPlanHousemateResponseDto.builder()
                .id(movingPlan.getId())
                .title(movingPlan.getTitle())
                .housemateId(housemate.getId())
                .isOwner(housemate.getIsOwner())
                .housemates(housemates.stream()
                        .map(HousemateResponseDto::from)
                        .toList())
                .build();
    }
}
