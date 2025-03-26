package com.example.p24zip.domain.house.entity;

import com.example.p24zip.domain.house.dto.request.ChangeHouseContentRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseDetailAddressRequestDto;
import com.example.p24zip.domain.house.dto.request.ChangeHouseNicknameRequestDto;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "house")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movingPlan_id", nullable = false)
    private MovingPlan movingPlan;

    @Column(length = 5, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String address1;
    @Column(length = 35, nullable = false)
    private String address2; // 상세 주소
    @Column(nullable = false)
    private double longitude; // 경도
    @Column(nullable = false)
    private double latitude; // 위도
    @Column(length = 1000)
    private String content;

    @Builder
    public House(MovingPlan movingPlan, String nickname, String address1,String address2,
        double latitude, double longitude, String content) {
        this.movingPlan = movingPlan;
        this.nickname = nickname;
        this.address1 = address1;
        this.address2 = address2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.content = content;
    }

    public House updateNickname(ChangeHouseNicknameRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        return this;
    }

    public House updateContent(ChangeHouseContentRequestDto requestDto) {
        this.content = requestDto.getContent();
        return this;
    }

    public House updateDetailAddress(ChangeHouseDetailAddressRequestDto requestDto) {
        this.address2 = requestDto.getAddress2();
        return this;
    }
}
