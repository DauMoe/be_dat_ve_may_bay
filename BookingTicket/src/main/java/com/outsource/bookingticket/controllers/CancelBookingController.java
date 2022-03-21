package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book/cancel")
public class CancelBookingController extends BaseController{

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping
    public ResponseEntity<?> cancelTicket(@RequestParam("ticket_id") Integer ticketId){
        ResponseCommon response = cancelBookingService.cancelTicket(ticketId);
        return ResponseEntity.ok(response);
    }
}
