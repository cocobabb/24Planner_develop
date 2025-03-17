package com.example.p24zip.domain.user.dto.request;


import com.example.p24zip.domain.user.entity.Role;
import com.example.p24zip.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Email
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String password;
    @NotBlank
    @Length(min = 2, max = 17)
    private String nickname;

    public User toEntity() {
        return User.builder()
            .username(username)
            .password(password)
            .nickname(nickname)
            .role(Role.ROLE_USER)
            .build();
    }


}
