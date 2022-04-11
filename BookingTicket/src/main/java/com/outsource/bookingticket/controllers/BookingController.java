package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/book/create")
public class BookingController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping
    public ResponseEntity<?> bookingFlight(@RequestHeader("Authorization") String token,
                                           @RequestBody BookingRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        ResponseCommon responseCommon = bookingService.bookingFlight(token, requestDto);
        return ResponseEntity.ok(responseCommon);
    }
}
