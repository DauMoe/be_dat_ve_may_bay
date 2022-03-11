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
                                  @RequestParam(value = "start_time", required = false) String startTime,
                                  @RequestParam(value = "end_time", required = false) String endTime) {
        return flightService.getDetailFlight(flightId, startTime, endTime);
    }

    @PostMapping(path = "/search")
    ResponseEntity<?> searchFlight(@RequestParam(value = "from_airport", required = false) Integer fromAirportId,
                                   @RequestParam(value = "to_airport", required = false) Integer toAirportId,
                                   @RequestParam(value = "flight_no", required = false) String flightNo) {
        return flightService.searchFlight(fromAirportId, toAirportId, flightNo);
    }
}
