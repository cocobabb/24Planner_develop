package com.example.p24zip.domain.house.dto.request;


import com.example.p24zip.domain.house.entity.House;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddHouseRequestDto {
    @NotBlank
    @Length(max = 5)
    private String nickname;
    @NotBlank
    private String address1;
    private String address2;

    public House toEntity(MovingPlan movingPlan) {
        return House.builder()
            .nickname(nickname)
            .address1(address1)
            .address2(address2)
            .movingPlan(movingPlan)
            .build();
    }
}
