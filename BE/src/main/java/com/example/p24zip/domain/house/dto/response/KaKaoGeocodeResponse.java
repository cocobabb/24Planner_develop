package com.example.p24zip.domain.house.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class KaKaoGeocodeResponse {
    @JsonProperty("documents")
    private List<Document> documents;

    @Getter
    public static class Document {
        @JsonProperty("address_name")
        private String addressName;
        @JsonProperty("x") // 경도 (longitude)
        private String longitude;
        @JsonProperty("y") // 위도 (latitude)
        private String latitude;
    }
}
