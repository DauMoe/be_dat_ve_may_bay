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
    private PriceDTO price;
    @JsonProperty("booking_state")
    private String bookingState;
    @JsonProperty("ticket_type")
    private String ticketType;
    @JsonProperty("airplane_detail")
    private AirplaneDTO airplaneDTO;
    @JsonProperty("flight_schedule")
    private FlightScheduleDTO flightSchedule;
    @JsonProperty("flight_detail")
    private FlightDetailDTO flightDTO;
    @JsonProperty("user")
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

    @Data
    public static class FlightDetailDTO {
        @JsonProperty("flight_id")
        private Integer flightId;
        @JsonProperty("flight_no")
        private String flightNo;
        @JsonProperty("from_airport")
        private LocationDTO fromAirport;
        @JsonProperty("to_airport")
        private LocationDTO toAirport;
    }

    @Data
    @AllArgsConstructor
    public static class PriceDTO {
        @JsonProperty("ticket_price")
        private Long ticketPrice;
        private Long tax;
        @JsonProperty("total_price")
        private Long totalPrice;
    }

}
