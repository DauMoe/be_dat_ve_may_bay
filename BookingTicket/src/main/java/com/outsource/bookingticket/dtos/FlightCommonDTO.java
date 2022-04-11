package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightCommonDTO {
    private Integer flightId;

    @JsonProperty("flight_no")
    private String flightNo;

    @JsonProperty("from_airport")
    private LocationDTO fromAirport;

    @JsonProperty("to_airport")
    private LocationDTO toAirport;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @JsonProperty("available_seat")
    private Integer availableSeat;

    @JsonProperty("airplane_name")
    private String airplaneName;


}
