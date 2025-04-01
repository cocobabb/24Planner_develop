package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationAcceptResponseDto;
import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HousemateService {

    private final MovingPlanRepository movingPlanRepository;
    private final HousemateRepository housemateRepository;

    @Transactional
    public HousemateInvitationAcceptResponseDto acceptInvitation(Long movingPlanId, User invitee) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."));

        if (housemateRepository.existsByUserAndMovingPlan(invitee, movingPlan)) {
            throw new CustomException("ALREADY_REGISTERED", "이미 이 플랜의 동거인으로 등록되어 있습니다.");
        }

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
