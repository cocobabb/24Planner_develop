package com.example.p24zip.domain.house.dto.request;

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
public class ChangeHouseDetailAddressRequestDto {
    @NotBlank
    @Length(max = 35)
    private String address2;

}
