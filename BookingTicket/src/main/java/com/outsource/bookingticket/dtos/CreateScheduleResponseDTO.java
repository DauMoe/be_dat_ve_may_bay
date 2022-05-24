package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleResponseDTO {
    @JsonProperty("flight_schedule")
    private ScheduleResponseDTO flightScheduleResponseDTO;
    private String message;

    @Data
    @NoArgsConstructor
    public static class ScheduleResponseDTO {
        @JsonProperty("flight_id")
        private Integer flightId;
        @JsonProperty("flight_no")
        private String flightNo;
        @JsonProperty("start_time")
        private String startTime;
        @JsonProperty("end_time")
        private String endTime;
        @JsonProperty("flight_schedule_id")
        private Integer flightScheduleId;
    }
}
