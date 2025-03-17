package com.example.p24zip.domain.user.dto.request;


import com.example.p24zip.domain.user.entity.Role;
import com.example.p24zip.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    private String email; //이메일이 아이디가 될 것임
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;

    public User toEntity() {
        return User.builder()
            .username(email)
            .password(password)
            .nickname(nickname)
            .role(Role.ROLE_USER)
            .build();
    }


}
