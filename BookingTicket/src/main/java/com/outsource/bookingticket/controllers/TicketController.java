package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController extends BaseController {
    @PutMapping(path = "/update")
    ResponseEntity<?> updateFlight(@RequestHeader("Authorization") String token,
                                   @RequestBody FlightUpdateRequestDTO flightUpdateRequestDTO) {
        return ticketService.updateFlight(flightUpdateRequestDTO, token);
    }
}
