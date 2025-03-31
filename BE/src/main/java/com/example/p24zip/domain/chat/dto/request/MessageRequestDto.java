package com.example.p24zip.domain.chat.dto.request;

import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageRequestDto {

    @NotBlank
    private String text;

    public Chat toEntity(MovingPlan movingPlan, User user) {
        return Chat.builder()
                .text(this.text)
                .movingPlan(movingPlan)
                .user(user)
                .build();
    }
}
