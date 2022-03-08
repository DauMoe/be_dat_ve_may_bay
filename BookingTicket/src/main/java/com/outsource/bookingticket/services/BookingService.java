package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.ticket.Ticket;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BookingService extends BaseService {

    public ResponseCommon bookingFlight(BookingRequestDto requestDto){

        if (Objects.nonNull(requestDto)) {
            Ticket ticket = new Ticket();
            ticket.setFlightScheduleId(requestDto.getFlightScheduleId());
            ticket.setSeatNumber(requestDto.getSeatNumber());
            ticket.setPrice(requestDto.getPrice());
            ticket.setBookingstate(requestDto.getBookingState());
        }
    }
}
