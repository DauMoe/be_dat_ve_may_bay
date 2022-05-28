package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleResponseDTO {
    @JsonProperty("flight_schedule_id")
    private Integer flightScheduleId;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("flight_no")
    private String flightNo;

    @JsonProperty("from_location")
    private LocationDTO fromLocation;

    @JsonProperty("to_location")
    private LocationDTO toLocation;

    @JsonProperty("airplane")
    private AirplaneDTO airplaneDTO;

    @JsonProperty("total_ticket_first_class")
    private Integer totalTicketFirstClass;

    @JsonProperty("total_ticket_business_class")
    private Integer totalTicketBusinessClass;

    @JsonProperty("total_ticket_premium_class")
    private Integer totalTicketPremiumClass;

    @JsonProperty("total_ticket_economy_class")
    private Integer totalTicketEconomyClass;
}

