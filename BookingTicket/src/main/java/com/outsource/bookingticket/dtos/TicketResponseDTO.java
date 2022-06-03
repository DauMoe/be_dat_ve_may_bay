package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.outsource.bookingticket.entities.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketResponseDTO {

    @JsonProperty("ticket_id")
    private Integer ticketId;
    @JsonProperty("row_seat")
    private String rowSeat;
    private String price;
    @JsonProperty("booking_state")
    private String bookingState;
    @JsonProperty("airplane_name")
    private String airplaneName;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("available_seat")
    private Integer availableSeat;
    @JsonProperty("flight_id")
    private Integer flightId;
    @JsonProperty("flight_no")
    private String flightNo;
    @JsonProperty("from_airport")
    private Location fromAirport;
    @JsonProperty("to_airport")
    private Location toAirport;
    @JsonProperty("airplane_id")
    private Integer airplaneId;
    private String username;

}
