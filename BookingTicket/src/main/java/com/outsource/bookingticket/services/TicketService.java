package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class TicketService extends BaseService {
    @Autowired
    private LogService logService;

    // Hàm thay đổi chuyến bay
    public ResponseEntity<?> updateFlight(FlightUpdateRequestDTO flightUpdateRequestDTO, String token) {
        // Lấy chuyến bay theo ID của chuyến bay
        Ticket ticket = getTicket(flightUpdateRequestDTO.getTicketId());

        // Tìm kiếm lịch trình bay cũ
        FlightSchedule flightScheduleOld = getFlightSchedule(ticket.getFlightScheduleId());

        // Tìm kiếm lịch trình bay mới
        FlightSchedule flightScheduleNew = getFlightSchedule(flightUpdateRequestDTO.getFlightScheduleId());
        // Thay đổi mã chuyến bay
        ticket.setFlightScheduleId(flightScheduleNew.getFlightScheduleId());

        // Gọi tới hàm thay đổi để thay đổi dưới DB
        ticketRepository.saveAndFlush(ticket);

        // Gọi tới hàm lưu log sau khi thay đổi
        logService.saveLogAfterUpdate(flightScheduleOld, flightScheduleNew, token);

        // Trả về thông báo thay đổi thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.FLIGHT_UPDATED_SUCCESS));
    }

    // Hàm lấy ra danh sách tất cả vé của user
    public ResponseEntity<?> getTickets(String token, String filter) {
        // Lấy ID của user từ token
        Integer userId = jwtTokenProvider.getUserIdFromJWT(getTokenFromHeader(token));

        // Lấy ra danh sách vé của user theo ID của user
        List<Ticket> tickets = ticketRepository.findByUid(userId);
        // Kiểm tra danh sách vé đó có rỗng hay không? nếu rỗng trả về lỗi
        if (!CollectionUtils.isEmpty(tickets)) {
            // khởi tạo 1 list để lưu thông tin vé
            List<Ticket> ticketList;
            // Kiểm tra filter truyền vào từ tham số có null hay không? nếu null thì gán ticketList = tickets
            if (Objects.nonNull(filter)) {
                // filter tickets theo tham số truyền vào ( BOOKED hoặc CANCELED)
                ticketList = tickets.stream()
                        .filter(t -> t.getBookingState().name().equals(filter.toUpperCase()))
                        .collect(Collectors.toList());
            } else ticketList = tickets;
            // Trả về response cho phía client
            return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(ticketList)));
        } else throw new ErrorException(MessageUtil.NOT_HAVE_ANY_TICKET);
    }

    // Hàm lấy ra thông tin chi tiết của 1 vé theo ID của vé
    public ResponseEntity<?> getDetailTicket(Integer ticketId) {
        // Tìm kiếm vé theo ID của vé
        Optional<Ticket> ticket = ticketRepository.findTicketByTicketId(ticketId);

        // Kiểm tra nếu vé tồn tại thì trả về response cho phía client. Nếu không tồn tại thì trả về lỗi
        if (ticket.isPresent()) {
            return ResponseEntity.ok(Helper.createSuccessCommon(ticket.get()));
        } else throw new ErrorException(MessageUtil.TICKET_NOT_FOUND);
    }
}
