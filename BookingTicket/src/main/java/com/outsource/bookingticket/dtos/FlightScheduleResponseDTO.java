package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightScheduleResponseDTO {
    @JsonProperty("flight_schedule_id")
    private Integer flightScheduleId;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @JsonProperty("flight_no")
    private String flightNo;

    @JsonProperty("available_seat")
    private Integer availableSeat;
}
