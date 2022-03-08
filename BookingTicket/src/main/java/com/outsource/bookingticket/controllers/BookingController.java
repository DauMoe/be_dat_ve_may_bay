package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/create")
public class BookingController extends BaseController {

    @PostMapping
    public ResponseEntity<?> bookingFlight(@RequestBody BookingRequestDto requestDto){
        ResponseCommon responseCommon = bookingService.bookingFlight(requestDto);
        return ResponseEntity.ok(responseCommon);
    }
}
