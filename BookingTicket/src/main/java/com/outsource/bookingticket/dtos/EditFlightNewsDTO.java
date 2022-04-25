package com.outsource.bookingticket.dtos;

import com.outsource.bookingticket.entities.flight_news.FlightNews;
import lombok.Data;

@Data
public class EditFlightNewsDTO {
    private FlightNews flightNews;
    private Integer numberOfExistingExtraImages;
}
