package com.example.p24zip.domain.user.dto.request;

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
public class ChangeNicknameRequestDto {

    @NotBlank
    @Length(min = 2, max = 17)
    private String nickname;
}
