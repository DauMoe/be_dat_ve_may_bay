package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.services.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description This class include all service are implemented
 */
public class BaseController {
    @Autowired protected UserService userService;
    @Autowired protected FlightService flightService;
    @Autowired protected LogService logService;
    @Autowired protected BookingService bookingService;
    @Autowired protected TicketService ticketService;
    @Autowired protected FlightNewsService flightNewsService;
    @Autowired protected LocationService locationService;
    @Autowired protected PassengerService passengerService;
    @Autowired protected FlightScheduleService flightScheduleService;
    @Autowired protected AirplaneService airplaneService;
}
