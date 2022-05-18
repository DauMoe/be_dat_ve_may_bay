package com.outsource.bookingticket.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/{ticket_id}")
    ResponseEntity<?> getDetailTicket(@PathVariable("ticket_id") Integer ticketId,
                                      @RequestParam(value = "adult", defaultValue = "1") Integer totalAdult,
                                      @RequestParam(value = "children", defaultValue = "0") Integer totalChildren,
                                      @RequestParam(value = "baby", defaultValue = "0") Integer totalBaby) {
        return ticketService.getDetailTicket(ticketId, totalAdult, totalChildren, totalBaby);
    }
}
