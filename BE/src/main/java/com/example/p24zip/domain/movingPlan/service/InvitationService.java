package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationValidateResponseDto;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    public HousemateInvitationResponseDto createHouseMateInvitation(Long movingPlanId,
        User inviter) {
        String token = jwtTokenProvider.invitationToken(movingPlanId, inviter);
        String shortId = generateShortId();

        redisTemplate.opsForValue().set(
            "short_invitation:" + shortId,
            token,
            24,
            TimeUnit.HOURS
        );

        String invitationLink = origin + "/invite?code=" + shortId;

        return HousemateInvitationResponseDto.from(invitationLink);
    }

    public HousemateInvitationValidateResponseDto validateInvitationCode(String shortId) {

        String token = redisTemplate.opsForValue().get("short_invitation:" + shortId);

        if (token == null) {
            throw new CustomException(CustomCode.INVALID_INVITATION);
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(CustomCode.INVALID_INVITATION);
        }

        Long movingPlanId = jwtTokenProvider.getMovingPlanId(token);
        Long inviterId = jwtTokenProvider.getInviterId(token);

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new CustomException(CustomCode.INVALID_INVITATION));
        User inviter = userRepository.findById(inviterId)
            .orElseThrow(() -> new CustomException(CustomCode.INVALID_INVITATION));

        return HousemateInvitationValidateResponseDto.from(movingPlan, inviter);
    }

    private String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
