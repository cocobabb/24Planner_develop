package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationValidateResponseDto;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {

    private final MovingPlanRepository movingPlanRepository;
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${ORIGIN}")
    private String origin;

    public HousemateInvitationResponseDto createHouseMateInvitation(Long movingPlanId, User inviter) {
        String token = jwtTokenProvider.invitationToken(movingPlanId, inviter);

        String invitationKey = "invitation:" + token;
        redisTemplate.opsForValue().set(
                invitationKey,
                String.valueOf(movingPlanId),
                24,
                TimeUnit.HOURS
        );

        String invitationLink = origin + "/invite?token=" + token;

        return HousemateInvitationResponseDto.from(invitationLink);
    }

    public HousemateInvitationValidateResponseDto validateInvitationToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다.");
        }

        Long movingPlanId = jwtTokenProvider.getMovingPlanId(token);
        Long inviterId = jwtTokenProvider.getInviterId(token);

        String invitationKey = "invitation:" + token;
        String storedPlanId = redisTemplate.opsForValue().get(invitationKey);

        if (storedPlanId == null || !storedPlanId.equals(String.valueOf(movingPlanId))) {
            throw new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다.");
        }

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."));
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."));

        return HousemateInvitationValidateResponseDto.from(movingPlan, inviter);
    }
}
