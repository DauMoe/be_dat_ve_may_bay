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
    @JsonProperty("ticket_id")
    private Integer ticketId;

    @JsonProperty("flight_schedule_id")
    private Integer flightScheduleId;
}
