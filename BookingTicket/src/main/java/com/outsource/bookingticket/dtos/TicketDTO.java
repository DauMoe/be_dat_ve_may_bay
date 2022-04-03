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
public class TicketDTO {
    @JsonProperty("ticket_id")
    private Integer ticketId;
    @JsonProperty("seat_number")
    private String seatNumber;
    private Long price;
    @JsonProperty("booking_state")
    private String bookingState;
    @JsonProperty("airplane_name")
    private String airplaneName;
    @JsonProperty("flight_schedule")
    private FlightScheduleDTO flightSchedule;
    @JsonProperty("flight_detail")
    private FlightResponseDTO flightDTO;
    @JsonProperty("user_detail")
    private UserDetailDTO userDetailDTO;

    @Data
    public static class FlightScheduleDTO {
        @JsonProperty("start_time")
        private String startTime;
        @JsonProperty("end_time")
        private String endTime;
        @JsonProperty("available_seat")
        private Integer availableSeat;
    }
}
