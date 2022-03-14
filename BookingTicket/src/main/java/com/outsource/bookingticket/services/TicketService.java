package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
        Ticket ticketUpdated = ticketRepository.saveAndFlush(ticket);

        // Gọi tới hàm lưu log sau khi thay đổi
        logService.saveLogAfterUpdate(flightScheduleOld, flightScheduleNew, token);

        // Trả về thông báo thay đổi thành công
        return ResponseEntity.ok(Helper.createSucessCommon(MessageUtil.FLIGHT_UPDATED_SUCCESS));
    }
}
