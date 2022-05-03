package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/book")
public class BookingController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(path = "/create")
    public ResponseEntity<?> bookingFlight(@RequestBody BookingRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        ResponseCommon responseCommon = bookingService.bookingFlight(requestDto);
        return ResponseEntity.ok(responseCommon);
    }
}
