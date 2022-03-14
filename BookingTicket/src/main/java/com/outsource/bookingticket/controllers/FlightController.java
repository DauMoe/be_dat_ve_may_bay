package com.outsource.bookingticket.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flight")
public class FlightController extends BaseController {

    @GetMapping(path = "/list")
    ResponseEntity<?> getAllFlight() {
        return flightService.getListFlight();
    }

    @GetMapping(path = "/get_info/{flight_id}")
    ResponseEntity<?> getFlightBy(@PathVariable("flight_id") Integer flightId,
                                  @RequestParam(value = "start_time", required = false) String startTime,
                                  @RequestParam(value = "end_time", required = false) String endTime) {
        return flightService.getDetailFlight(flightId, startTime, endTime);
    }

    @GetMapping(path = "/search")
    ResponseEntity<?> searchFlight(@RequestParam(value = "from_airport", required = false) Integer fromAirportId,
                                   @RequestParam(value = "to_airport", required = false) Integer toAirportId,
                                   @RequestParam(value = "flight_no", required = false) String flightNo) {
        return flightService.searchFlight(fromAirportId, toAirportId, flightNo);
    }

    @GetMapping(path = "/list-schedule")
    ResponseEntity<?> getAllFlightSchedule() {
        return flightService.getAllFlightSchedule();
    }
}
