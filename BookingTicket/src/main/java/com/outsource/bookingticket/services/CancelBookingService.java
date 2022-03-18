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

    // Hàm hủy vé máy bay
    public ResponseCommon cancelTicket(Integer ticketId){

        // Hàm tìm thông tin vé theo Id của vé
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        // Kiểm tra vé tìm được có phải rỗng hay không? nếu rỗng trả về lỗi.
        if (ticket.isEmpty()) throw new ErrorException("Không Tìm Thấy Vé");
        // Kiểm tra vé đó đã bị hủy từ trước hay chưa? nếu đã bị hủy từ trước rồi thì trả về lỗi
        if (ticket.get().getBookingState().equals(BOOKINGSTATE.CANCELED)) throw new ErrorException("Vé Đã Bị Hủy");
        // Thay đổi trạng thái vé từ BOOKED sang CANCELED
        ticket.get().setBookingState(BOOKINGSTATE.CANCELED);
        // Cập nhật thông tin vé vào database
        ticketRepository.saveAndFlush(ticket.get());

        // Trả về các thông tin cho phía client.
        ResponseCommon response = new ResponseCommon();
        response.setCode(200);
        response.setResult("Hủy Vé Thành Công");

        return response;
    }
}
