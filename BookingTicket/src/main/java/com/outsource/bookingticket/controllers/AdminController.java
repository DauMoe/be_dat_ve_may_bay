package com.outsource.bookingticket.controllers;

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

    // API khoá chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PutMapping(path = "/lock-flight/{flight_id}")
    ResponseEntity<?> getAllFlight(@PathVariable("flight_id") Integer flightId) {
        return flightService.updateFlightState(flightId);
    }
}
