package com.example.p24zip.domain.house.dto.request;

import com.example.p24zip.domain.house.entity.House;
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
public class ChangeHouseNicknameRequestDto {
    @NotBlank
    @Length(max = 5)
    private String nickname;

}
