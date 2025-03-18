package com.example.p24zip.domain.user.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class VerifyEmailDataResponseDto {
    @NotNull
    private LocalDateTime expiredAt;

    public static VerifyEmailDataResponseDto from (LocalDateTime expiredAt){
        return VerifyEmailDataResponseDto.builder()
            .expiredAt(expiredAt)
            .build();
    }


}
