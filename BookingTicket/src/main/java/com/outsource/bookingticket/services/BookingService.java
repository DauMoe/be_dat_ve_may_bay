package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
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
            ticket.setBookingState(requestDto.getBookingState());
            ticket.setUid(1);

            ticketRepository.save(ticket);
        }else throw new ErrorException("INVALID_REQUEST");

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult("Booking vé thành công");
        return responseCommon;
    }
}
