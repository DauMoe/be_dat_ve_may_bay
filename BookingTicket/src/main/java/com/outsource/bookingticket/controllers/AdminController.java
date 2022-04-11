package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

    // API lấy hết danh sách thông tin chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/list-flight")
    ResponseEntity<?> getAllFlight(@RequestParam(value = "from_airport", required = false) Integer fromAirportId,
                                   @RequestParam(value = "to_airport", required = false) Integer toAirportId,
                                   @RequestParam(value = "flight_no", required = false) String flightNo) {
        return flightService.getAllFlight(fromAirportId, toAirportId, flightNo);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/list-ticket")
    ResponseEntity<?> getAllTicket(@RequestParam(value = "flight_schedule_id") Integer flightScheduleId) {
        return ticketService.getAllTicketByScheduleId(flightScheduleId);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/cancel-ticket")
    public ResponseEntity<?> cancelTicket(@RequestParam("ticket_id") Integer ticketId){
        ResponseCommon response = ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok(response);
    }

    // API khoá chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/lock-flight/{flight_id}")
    ResponseEntity<?> getAllFlight(@PathVariable("flight_id") Integer flightId) {
        return flightService.updateFlightState(flightId);
    }

}
