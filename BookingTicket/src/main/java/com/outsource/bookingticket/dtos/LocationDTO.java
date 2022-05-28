package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO {
    @JsonProperty("location_id")
    private Integer locationId;
    @JsonProperty("airport_name")
    private String airportName;
    private String city;
    private String country;
}
