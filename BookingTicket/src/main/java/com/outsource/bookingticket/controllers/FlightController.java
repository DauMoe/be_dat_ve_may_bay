package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flight")
public class FlightController extends BaseController {

    @PostMapping(path = "/update")
    ResponseEntity<?> updateFlight(@RequestHeader("Authorization") String token,
                                   @RequestBody FlightUpdateRequestDTO flightUpdateRequestDTO) {
        return flightService.updateFlight(flightUpdateRequestDTO, token);
    }

    @PostMapping(path = "/list")
    ResponseEntity<?> getAllFlight() {
        return flightService.getListFlight();
    }

    @PostMapping(path = "/get_info/{flight_id}")
    ResponseEntity<?> getFlightBy(@PathVariable("flight_id") Integer flightId,
                                  @RequestParam("start_time") String startTime,
                                  @RequestParam("end_time") String endTime) {
        return flightService.getDetailFlight(flightId, startTime, endTime);
    }


}
