package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CancelBookingService extends BaseService {

    public ResponseCommon cancelTicket(Integer ticketId){

        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) throw new ErrorException("Không Tìm Thấy Vé");
        if (ticket.get().getBookingState().equals(BOOKINGSTATE.CANCELED)) throw new ErrorException("Vé Đã Bị Hủy");

        ticket.get().setBookingState(BOOKINGSTATE.CANCELED);
        ticketRepository.saveAndFlush(ticket.get());

        ResponseCommon response = new ResponseCommon();
        response.setCode(200);
        response.setResult("Hủy Vé Thành Công");

        return response;
    }
}
