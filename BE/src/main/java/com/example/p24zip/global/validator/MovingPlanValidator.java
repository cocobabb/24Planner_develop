package com.example.p24zip.global.validator;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MovingPlanValidator {

    private final MovingPlanRepository movingPlanRepository;

    public MovingPlanValidator(MovingPlanRepository movingPlanRepository) {
        this.movingPlanRepository = movingPlanRepository;
    }

    public void validateMovingPlanAccess(Long movingPlanId, User user) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(ResourceNotFoundException::new);

        if (!isMovingPlanMember(movingPlan, user)) {
            throw new ResourceNotFoundException();
        }
    }

    public void validateMovingPlanOwnership(Long movingPlanId, User user) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(ResourceNotFoundException::new);

        if (!isMovingPlanOwner(movingPlan, user)) {
            throw new ResourceNotFoundException();
        }
    }

    private boolean isMovingPlanMember(MovingPlan movingPlan, User user) {
        return movingPlan.getHousemates().stream()
                .anyMatch(housemate -> housemate.getUser().getId().equals(user.getId()));
    }

    private boolean isMovingPlanOwner(MovingPlan movingPlan, User user) {
        return movingPlan.getHousemates().stream()
                .anyMatch(housemate ->
                        housemate.getUser().getId().equals(user.getId()) &&
                        housemate.getIsOwner()
                );
    }
}
