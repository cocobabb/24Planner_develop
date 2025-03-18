package com.example.p24zip.domain.user.dto.request;


import com.example.p24zip.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequestDto {

    @NotBlank
    @Email
    private String username;


    public User toEntity() {
        return User.builder()
            .username(username)
            .build();
    }

}
