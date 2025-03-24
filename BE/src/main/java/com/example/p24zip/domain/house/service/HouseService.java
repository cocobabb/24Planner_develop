package com.example.p24zip.domain.house.service;

import com.example.p24zip.domain.house.dto.request.AddHouseRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseContentRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseNicknameRequestDto;
import com.example.p24zip.domain.house.dto.response.AddHouseResponseDto;
import com.example.p24zip.domain.house.dto.response.ChangeHouseContentResponseDto;
import com.example.p24zip.domain.house.dto.response.ChangeHouseNicknameResponseDto;
import com.example.p24zip.domain.house.dto.response.GetHouseDetailsResponseDto;
import com.example.p24zip.domain.house.dto.response.HouseListResponseDto;
import com.example.p24zip.domain.house.dto.response.KaKaoGeocodeResponse;
import com.example.p24zip.domain.house.dto.response.KaKaoGeocodeResponse.Document;
import com.example.p24zip.domain.house.entity.House;
import com.example.p24zip.domain.house.repository.HouseRepository;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.global.exception.GeocoderExceptionHandler;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;
    private final MovingPlanRepository movingPlanRepository;

    @Value("${KAKAO_RESTAPI_KEY}")
    private String restApiKey;


    /**
     * @param movingPlanId : 집 관련 테이블과 연관관계를 가진 테이블의 id
     * @param requestDto 집 별칭, 검색 주소, 상세 주소
     * @return 집 id, 집 별칭, 검색 주소, 상세 주소, 위도, 경도(검색 주소로 변환된 위도, 경도 값 응답해줌)
     * **/
    @Transactional
    public AddHouseResponseDto postHouse(Long movingPlanId, AddHouseRequestDto requestDto) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId).orElseThrow(ResourceNotFoundException::new);
        House house = requestDto.toEntity(movingPlan);

        String address1 = requestDto.getAddress1();
        Document location = Geocoder(address1);
        double latitude = Double.parseDouble(location.getLatitude());
        double longitude = Double.parseDouble(location.getLongitude());

        house.setLatitude(latitude);
        house.setLongitude(longitude);
        houseRepository.save(house);

        return AddHouseResponseDto.from(house);

    }


    /**
     * @param movingPlanId : 집 관련 테이블과 연관관계를 가진 테이블의 id
     * @return 집 정보를 가진 리스트
     * **/
    public HouseListResponseDto getHouses(Long movingPlanId) {
       MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId).orElseThrow(ResourceNotFoundException::new);

        List<House> houseList = houseRepository.findAllByMovingPlan(movingPlan);
        return HouseListResponseDto.from(houseList);
    }

    
    /**
     * @param houseId : 집 테이블의 id
     * @return responseDto (id, nickname, address1, address2, content)
     * **/
    public GetHouseDetailsResponseDto getHouseDetails(Long movingPlanId, Long houseId) {
        isMovingPlanIdMatched(movingPlanId, houseId);
        return GetHouseDetailsResponseDto.from(houseRepository.findById(houseId).orElseThrow(ResourceNotFoundException::new));
    }

    
    /**
     * @param houseId : 집 테이블의 id
     * @param requestDto : nickname
     * @return responseDto (id, nickname)
     * **/
    @Transactional
    public ChangeHouseNicknameResponseDto updateHouseNickname(Long movingPlanId, Long houseId, ChangeHouseNicknameRequestDto requestDto) {
        isMovingPlanIdMatched(movingPlanId, houseId);
        House house = houseRepository.findById(houseId).orElseThrow(ResourceNotFoundException::new);
        house.updateNickname(requestDto);

        return ChangeHouseNicknameResponseDto.from(house);
    }
    

    /**
     * @param houseId : 집 테이블의 id
     * @param requestDto : content
     * @return responseDto (id, nickname)
     * **/
    @Transactional
    public ChangeHouseContentResponseDto updateHouseContent(Long movingPlanId, Long houseId, ChangeHouseContentRequestDto requestDto) {
        isMovingPlanIdMatched(movingPlanId, houseId);
        House house = houseRepository.findById(houseId).orElseThrow(ResourceNotFoundException::new);
        house.updateContent(requestDto);

        return ChangeHouseContentResponseDto.from(house);
    }

    
    /**
     * @param houseId : 집 테이블의 id
     * @return null
     * **/
    @Transactional
    public void deleteHouse(Long movingPlanId, Long houseId) {
        isMovingPlanIdMatched(movingPlanId, houseId);
        houseRepository.deleteById(houseId);
    }

    ///////////////////////////////////////////////////////////////////////////////
    // 보조 메서드
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * @param address1 : 위도, 경도로 변환할 검색한 주소
     * @return location : 변환한 위도, 경도 값을 가지고 있는 객체
     * **/
    private Document Geocoder(String address1) {

        WebClient webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com/v2/local/search/address.json")
            .build();

        KaKaoGeocodeResponse coordinates = webClient.get().uri(uriBuilder -> uriBuilder.queryParam("query",address1).build())
            .header("Authorization", "KakaoAK " + restApiKey)
            .retrieve()
            .bodyToMono(KaKaoGeocodeResponse.class).block();

        if(coordinates != null && !coordinates.getDocuments().isEmpty()){
            KaKaoGeocodeResponse.Document location = coordinates.getDocuments().get(0);
            return location;
        }else {
            throw new GeocoderExceptionHandler("GEOCODER_API_CONVERT_ERROR","좌표 변경 API에서 변환 오류가 발생했습니다.");
        }
    }
    
    
    /**
     * 해당 이사계획에 속한 집이 아닐 경우 예외 처리
     * @param movingPlanId : 이사계획 테이블 id
     * @param houseId : 집 테이블 id (이사계획 테이블의 id FK로 연관 관계 가지고 있음)
     * @return null
     * **/
    private void isMovingPlanIdMatched(Long movingPlanId, Long houseId) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId).orElseThrow(ResourceNotFoundException::new);
        if(houseRepository.findById(houseId).orElseThrow(ResourceNotFoundException::new).getMovingPlan() != movingPlan) {
            throw new ResourceNotFoundException();
        }
    }


}
