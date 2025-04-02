package com.example.p24zip.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class OAuthSignupRequestDto {

    @NotBlank
    private String tempToken;

    @NotBlank
    @Length(min = 2, max = 17)
    private String nickname;

}
