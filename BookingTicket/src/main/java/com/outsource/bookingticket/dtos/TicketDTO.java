package com.outsource.bookingticket.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    @JsonProperty("row_seat")
    private String rowSeat;
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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "adult_price", "children_price", "baby_price", "tax", "total_price" })
    public static class PriceDTO {
        @JsonProperty("adult_price")
        private DetailPriceDTO adultPrice;
        @JsonProperty("children_price")
        private DetailPriceDTO childrenPrice;
        @JsonProperty("baby_price")
        private DetailPriceDTO babyPrice;
        private String tax;
        @JsonProperty("total_price")
        private String totalPrice;
    }

}
