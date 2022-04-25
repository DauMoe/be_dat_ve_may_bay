package com.outsource.bookingticket.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flight")
public class FlightController extends BaseController {

    // API xem chi tiết thông tin của chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/get_info/{flight_id}")
    ResponseEntity<?> getFlightBy(@PathVariable("flight_id") Integer flightId) {
        return flightService.getDetailFlight(flightId);
    }

    // API tìm kiếm thông tin chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/search")
    ResponseEntity<?> searchFlight(@RequestParam(value = "from_airport") Integer fromAirportId,
                                   @RequestParam(value = "to_airport") Integer toAirportId,
                                   @RequestParam(value = "start_time") String startTime,
                                   @RequestParam(value = "end_time", required = false) String endTime) {
        return flightService.searchFlight(fromAirportId, toAirportId, startTime, endTime);
    }

    // API lấy danh sách lịch trình chuyến bay
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(path = "/list-schedule")
    ResponseEntity<?> getAllFlightSchedule() {
        return flightService.getAllFlightSchedule();
    }
}
