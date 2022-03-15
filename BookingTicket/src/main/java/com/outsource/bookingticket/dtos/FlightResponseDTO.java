package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightResponseDTO {
    private Integer flightId;

    @JsonProperty("flight_no")
    private String flightNo;

    @JsonProperty("from_airport")
    private LocationDTO fromAirport;

    @JsonProperty("to_airport")
    private LocationDTO toAirport;

    @JsonProperty("airplane_id")
    private Integer airplaneId;
}
