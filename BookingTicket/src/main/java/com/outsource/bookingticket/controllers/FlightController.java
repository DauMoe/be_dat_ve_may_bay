package com.outsource.bookingticket.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flight")
public class FlightController extends BaseController {

    // API tìm kiếm thông tin chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/search")
    ResponseEntity<?> searchFlight(@RequestParam(value = "from_airport") Integer fromAirportId,
                                   @RequestParam(value = "to_airport") Integer toAirportId,
                                   @RequestParam(value = "start_time") String startTime,
                                   @RequestParam(value = "end_time", required = false) String endTime,
                                   @RequestParam(value = "adult", defaultValue = "1") Integer totalAdult,
                                   @RequestParam(value = "children", defaultValue = "0") Integer totalChildren,
                                   @RequestParam(value = "baby", defaultValue = "0") Integer totalBaby) {
        return flightService.searchFlight(fromAirportId, toAirportId, startTime, endTime, totalAdult, totalChildren, totalBaby);
    }
}
