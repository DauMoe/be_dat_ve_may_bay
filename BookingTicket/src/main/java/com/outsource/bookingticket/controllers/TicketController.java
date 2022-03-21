package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/update")
    ResponseEntity<?> updateFlight(@RequestHeader("Authorization") String token,
                                   @RequestBody FlightUpdateRequestDTO flightUpdateRequestDTO) {
        return ticketService.updateFlight(flightUpdateRequestDTO, token);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping
    ResponseEntity<?> getTickets(@RequestHeader("Authorization") String token,
                                 @RequestParam(value = "filter", required = false) String filter) {
        return ticketService.getTickets(token, filter);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/{ticket_id}")
    ResponseEntity<?> getDetailTicket(@PathVariable("ticket_id") Integer ticketId) {
        return ticketService.getDetailTicket(ticketId);
    }
}
