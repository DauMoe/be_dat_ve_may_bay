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

    public ResponseEntity<?> getTickets(String token, String filter) {
        Integer userId = jwtTokenProvider.getUserIdFromJWT(getTokenFromHeader(token));

        List<Ticket> tickets = ticketRepository.findByUid(userId);
        if (!CollectionUtils.isEmpty(tickets)) {
            List<Ticket> ticketList;
            if (Objects.nonNull(filter)) {
                ticketList = tickets.stream()
                        .filter(t -> t.getBookingState().name().equals(filter.toUpperCase()))
                        .collect(Collectors.toList());
            } else ticketList = tickets;
            return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(ticketList)));
        } else throw new ErrorException(MessageUtil.NOT_HAVE_ANY_TICKET);
    }

    public ResponseEntity<?> getDetailTicket(Integer ticketId) {
        Optional<Ticket> ticket = ticketRepository.findTicketByTicketId(ticketId);

        if (ticket.isPresent()) {
            return ResponseEntity.ok(Helper.createSuccessCommon(ticket.get()));
        } else throw new ErrorException(MessageUtil.TICKET_NOT_FOUND);
    }
}
