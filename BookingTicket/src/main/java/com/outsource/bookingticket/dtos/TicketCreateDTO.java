package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketCreateDTO {

    @JsonProperty("flight_schedule_id")
    private Integer flightScheduleId;

    @JsonProperty("first_class_number")
    private Integer firstClassNumber;

    @JsonProperty("business_class_number")
    private Integer businessClassNumber;

    @JsonProperty("premium_class_number")
    private Integer premiumClassNumber;

    @JsonProperty("economy_class_number")
    private Integer economyClassNumber;
}
