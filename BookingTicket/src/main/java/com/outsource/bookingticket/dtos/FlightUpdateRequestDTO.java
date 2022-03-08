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
public class FlightUpdateRequestDTO {
    @JsonProperty("flight_id")
    private Integer flightId;

    @JsonProperty("from_airport_id")
    private Integer fromAirportId;

    @JsonProperty("to_airport_id")
    private Integer toAirportId;

    @JsonProperty("airplane_id")
    private Integer airplaneId;
}