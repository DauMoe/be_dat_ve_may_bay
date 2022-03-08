package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.services.FlightService;
import com.outsource.bookingticket.services.LogService;
import com.outsource.bookingticket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description This class include all service are implemented
 */
public class BaseController {
    @Autowired protected UserService userService;
    @Autowired protected FlightService flightService;
    @Autowired protected LogService logService;
}
