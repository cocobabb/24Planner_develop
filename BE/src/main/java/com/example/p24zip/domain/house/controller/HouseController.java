package com.example.p24zip.domain.house.controller;

import com.example.p24zip.domain.house.dto.request.AddHouseRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseContentRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseDetailAddressRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseNicknameRequestDto;
import com.example.p24zip.domain.house.dto.response.AddHouseResponseDto;
import com.example.p24zip.domain.house.dto.response.ChangeHouseContentResponseDto;
import com.example.p24zip.domain.house.dto.response.ChangeHouseDetailAddressResponseDto;
import com.example.p24zip.domain.house.dto.response.ChangeHouseNicknameResponseDto;
import com.example.p24zip.domain.house.dto.response.GetHouseDetailsResponseDto;
import com.example.p24zip.domain.house.dto.response.HouseListResponseDto;
import com.example.p24zip.domain.house.service.HouseService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.validator.MovingPlanValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/{movingPlanId}/houses")
public class HouseController {

    private final HouseService houseService;
    private final MovingPlanValidator movingPlanValidator;

    @PostMapping
    public ResponseEntity<ApiResponse<AddHouseResponseDto>> postHouse(
        @PathVariable Long movingPlanId, @RequestBody @Valid AddHouseRequestDto requestDto,
        @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_CREATE_SUCCESS.getCode(),
                CustomCode.HOUSE_CREATE_SUCCESS.getMessage(),
                houseService.postHouse(movingPlanId, requestDto))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HouseListResponseDto>> getHouses(
        @PathVariable Long movingPlanId, @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_COLLECTIONS_LOAD_SUCCESS.getCode(),
                CustomCode.HOUSE_COLLECTIONS_LOAD_SUCCESS.getMessage(),
                houseService.getHouses(movingPlanId))
        );
    }

    @GetMapping("/{houseId}")
    public ResponseEntity<ApiResponse<GetHouseDetailsResponseDto>> getHouseDetails(
        @PathVariable Long movingPlanId, @PathVariable Long houseId,
        @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_LOAD_SUCCESS.getCode(),
                CustomCode.HOUSE_LOAD_SUCCESS.getMessage(),
                houseService.getHouseDetails(movingPlanId, houseId))
        );
    }

    @PatchMapping("/{houseId}/nickname")
    public ResponseEntity<ApiResponse<ChangeHouseNicknameResponseDto>> updateHouseNickname(
        @PathVariable Long movingPlanId, @PathVariable Long houseId,
        @RequestBody @Valid ChangeHouseNicknameRequestDto requestDto,
        @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_NICKNAME_UPDATE_SUCCESS.getCode(),
                CustomCode.HOUSE_NICKNAME_UPDATE_SUCCESS.getMessage(),
                houseService.updateHouseNickname(movingPlanId, houseId, requestDto))
        );
    }

    @PatchMapping("/{houseId}/content")
    public ResponseEntity<ApiResponse<ChangeHouseContentResponseDto>> updateHouseContent(
        @PathVariable Long movingPlanId, @PathVariable Long houseId,
        @RequestBody @Valid ChangeHouseContentRequestDto requestDto,
        @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_DETAIL_CONTENT_UPDATE_SUCCESS.getCode(),
                CustomCode.HOUSE_DETAIL_CONTENT_UPDATE_SUCCESS.getMessage(),
                houseService.updateHouseContent(movingPlanId, houseId, requestDto)
            )
        );
    }

    @PatchMapping("/{houseId}/detail-address")
    public ResponseEntity<ApiResponse<ChangeHouseDetailAddressResponseDto>> updateHouseDetailAddress(
        @PathVariable Long movingPlanId, @PathVariable Long houseId,
        @RequestBody @Valid ChangeHouseDetailAddressRequestDto requestDto,
        @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_DETAIL_ADDRESS_UPDATE_SUCCESS.getCode(),
                CustomCode.HOUSE_DETAIL_ADDRESS_UPDATE_SUCCESS.getMessage(),
                houseService.updateHouseDetailAddress(movingPlanId, houseId, requestDto)
            )
        );
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<ApiResponse<Void>> deleteHouse(@PathVariable Long movingPlanId,
        @PathVariable Long houseId, @AuthenticationPrincipal User user) {
        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        houseService.deleteHouse(movingPlanId, houseId);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.HOUSE_DELETE_SUCCESS.getCode(),
                CustomCode.HOUSE_DELETE_SUCCESS.getMessage(),
                null
            )
        );
    }


}