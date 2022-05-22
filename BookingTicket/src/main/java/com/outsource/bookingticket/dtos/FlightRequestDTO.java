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
public class FlightRequestDTO {
    @JsonProperty("flight_no")
    private String flightNo;
    @JsonProperty("from_airport_id")
    private Integer fromAirportId;
    @JsonProperty("to_airport_id")
    private Integer toAirportId;
    @JsonProperty("airplane_id")
    private Integer airplaneId;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
}
