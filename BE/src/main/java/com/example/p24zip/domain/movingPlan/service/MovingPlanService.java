package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanResponseDto;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovingPlanService {

    private final MovingPlanRepository movingPlanRepository;

    @Transactional
    public MovingPlanResponseDto createMovingPlan(MovingPlanRequestDto requestDto, User user) {
        return MovingPlanResponseDto.from(movingPlanRepository.save(requestDto.toEntity(user)));
    }

    public List<MovingPlanResponseDto> readMovingPlans(User user) {
        return movingPlanRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(MovingPlanResponseDto::from)
                .toList();
    }

    public MovingPlanResponseDto readMovingPlanById(Long id) {
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
