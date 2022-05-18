package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDTO {
    @JsonProperty("city_name")
    private String cityName;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("airport_name")
    private String airportName;
}
