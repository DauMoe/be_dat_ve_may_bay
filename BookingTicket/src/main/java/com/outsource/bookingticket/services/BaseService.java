package com.outsource.bookingticket.services;

import com.outsource.bookingticket.mapper.ModelMapper;
import com.outsource.bookingticket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
    @Autowired protected UserRepository userRepository;
//    @Autowired protected ModelMapper modelMapper;
}
