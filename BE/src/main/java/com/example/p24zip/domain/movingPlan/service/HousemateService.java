package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationAcceptResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateNotificationDto;
import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.p24zip.global.exception.CustomErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HousemateService {

    private final MovingPlanRepository movingPlanRepository;
    private final HousemateRepository housemateRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public HousemateInvitationAcceptResponseDto acceptInvitation(Long movingPlanId, User invitee) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_INVITATION));

        if (housemateRepository.existsByUserAndMovingPlan(invitee, movingPlan)) {
            throw new CustomException(CustomErrorCode.ALREADY_REGISTERED);
        }

        // 기존 Housemate 목록 조회
        List<Housemate> existingHousemates = housemateRepository.findByMovingPlan(movingPlan);
        List<String> existingHousemateUsernames = existingHousemates.stream()
            .map(h -> h.getUser().getUsername())
            .collect(Collectors.toList());

        // 알림 DTO 생성
        HousemateNotificationDto notificationDto = HousemateNotificationDto.builder()
            .newHousemateUsername(invitee.getUsername())
            .movingPlanTitle(movingPlan.getTitle())
            .existingHousemateUsernames(existingHousemateUsernames)
            .build();

        // 기존 Housemate들에게 알림 전송
        notificationService.publishNotification(notificationDto, movingPlanId);

        Housemate housemate = Housemate.createHousemate(invitee, movingPlan);
        housemateRepository.save(housemate);

        return HousemateInvitationAcceptResponseDto.from(housemate);
    }

    @Transactional
    public void deleteHousemate(Long id) {
        Housemate housemate = housemateRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        housemateRepository.delete(housemate);
    }
}
