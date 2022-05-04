package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationResponseDTO {
    @JsonProperty("country_name")
    private String countryName;
    @JsonProperty("cities")
    List<LocationDTO> locationDTOs = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class LocationDTO {
        @JsonProperty("location_id")
        private Integer locationId;
        @JsonProperty("city_name")
        private String cityName;
    }
}
