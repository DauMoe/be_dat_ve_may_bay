package com.outsource.bookingticket.services;

import com.outsource.bookingticket.repositories.FlightLogRepository;
import com.outsource.bookingticket.repositories.FlightScheduleRepository;
import com.outsource.bookingticket.repositories.TicketRepository;
import com.outsource.bookingticket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {

    @Autowired protected UserRepository userRepository;

    @Autowired protected FlightLogRepository flightLogRepository;

    @Autowired protected TicketRepository ticketRepository;

    @Autowired protected FlightScheduleRepository flightScheduleRepository;
}
