package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description This class include all service are implemented
 */
public class BaseController {
    @Autowired protected UserService userService;
}
