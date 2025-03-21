package com.example.p24zip.domain.house.controller;

import com.example.p24zip.domain.house.dto.request.AddHouseRequestDto;
import com.example.p24zip.domain.house.dto.response.AddHouseResponseDto;
import com.example.p24zip.domain.house.dto.response.HouseListResponseDto;
import com.example.p24zip.domain.house.service.HouseService;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    public ResponseEntity<ApiResponse<AddHouseResponseDto>> postHouse(@PathVariable Long movingPlanId, @RequestBody @Valid AddHouseRequestDto requestDto){
        return ResponseEntity.ok(
            ApiResponse.ok("CREATED", "집 생성에 성공했습니다.", houseService.postHouse(movingPlanId, requestDto))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HouseListResponseDto>> getHouses(@PathVariable Long movingPlanId) {
        return ResponseEntity.ok(
          ApiResponse.ok("OK", "집 목록을 조회에 성공했습니다.", houseService.getHouses(movingPlanId))
        );
    }

}
