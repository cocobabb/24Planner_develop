package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.controller.NotificationController;
import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanHousemateResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanOwnerResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanResponseDto;
import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import com.example.p24zip.global.notification.SseEmitterPool;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovingPlanService {

    private final MovingPlanRepository movingPlanRepository;
    private final HousemateRepository housemateRepository;

    private final SseEmitterPool sseEmitterPool;
    private final NotificationController notificationController;

    @Transactional
    public MovingPlanOwnerResponseDto createMovingPlan(MovingPlanRequestDto requestDto, User user) {
        MovingPlan movingPlan = movingPlanRepository.save(requestDto.toEntity());

        Housemate owner = Housemate.createOwner(user, movingPlan);
        housemateRepository.save(owner);

        return MovingPlanOwnerResponseDto.from(owner);
    }

    public List<MovingPlanOwnerResponseDto> readMovingPlans(User user) {
        // sse 연결
        sseEmitterPool.connect(user.getUsername());
        // 로그인 시 안읽은 알림에 대해서 알림 전송
        notificationController.checkUnreadNotification(user);

        return housemateRepository.findByUserOrderByMovingPlanCreatedAtDesc(user).stream()
            .map(MovingPlanOwnerResponseDto::from)
            .toList();
    }

    public MovingPlanHousemateResponseDto readMovingPlanById(Long id, User user) {
        MovingPlan movingPlan = movingPlanRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        Housemate housemate = housemateRepository.findByUserAndMovingPlan(user, movingPlan);

        List<Housemate> housemates = housemateRepository.findByMovingPlan(movingPlan);

        return MovingPlanHousemateResponseDto.from(movingPlan, housemate, housemates);
    }

    public MovingPlanResponseDto readMovingPlanTitleById(Long id) {
        MovingPlan movingPlan = movingPlanRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        return MovingPlanResponseDto.from(movingPlan);
    }

    @Transactional
    public MovingPlanResponseDto updateMovingPlan(Long id, MovingPlanRequestDto requestDto) {
        MovingPlan movingPlan = movingPlanRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);
        movingPlan.update(requestDto);

        return MovingPlanResponseDto.from(movingPlan);
    }

    @Transactional
    public void deleteMovingPlan(Long id) {
        MovingPlan movingPlan = movingPlanRepository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);

        movingPlanRepository.delete(movingPlan);
    }
}
