package com.outsource.bookingticket.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/{ticket_id}")
    ResponseEntity<?> getDetailTicket(@PathVariable("ticket_id") Integer ticketId) {
        return ticketService.getDetailTicket(ticketId);
    }
}
