package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class BookingService extends BaseService {

    public ResponseCommon bookingFlight(BookingRequestDto requestDto) {

        if (Objects.nonNull(requestDto)) {

            Optional<FlightSchedule> flightSchedule = flightScheduleRepository.findById(requestDto.getFlightScheduleId());
            if (flightSchedule.isEmpty()) throw new ErrorException("Chuyến Bay Không Tồn Tại");

            Ticket ticketExist = ticketRepository
                    .findBySeatNumberAndFlightScheduleId(requestDto.getSeatNumber(), requestDto.getFlightScheduleId());
            if (Objects.nonNull(ticketExist)
                    && ticketExist.getBookingState().equals(BOOKINGSTATE.BOOKED))
                throw new ErrorException("Số Ghế Đã Được Đặt");

            Ticket ticket = new Ticket();
            ticket.setFlightScheduleId(flightSchedule.get().getFlightScheduleId());
            ticket.setSeatNumber(requestDto.getSeatNumber());
            ticket.setPrice(requestDto.getPrice());
            ticket.setBookingState(BOOKINGSTATE.BOOKED);
            ticket.setUid(1);
            ticketRepository.save(ticket);

            flightSchedule.get().setAvailableSeat(flightSchedule.get().getAvailableSeat() - 1);
            flightScheduleRepository.saveAndFlush(flightSchedule.get());


        } else throw new ErrorException("INVALID_REQUEST");

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult("Booking Vé Thành Công");
        return responseCommon;
    }
}
