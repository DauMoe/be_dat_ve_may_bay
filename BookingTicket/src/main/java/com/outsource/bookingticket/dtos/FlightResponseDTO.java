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

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("weight_package")
    private Integer weightPackage;

    private Long price;

    @JsonProperty("flight_schedule_id")
    private Integer flightScheduleId;

    @JsonProperty("ticket_id")
    private Integer ticketId;
}
