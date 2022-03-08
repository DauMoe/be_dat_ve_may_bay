package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.exception.ErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class FlightService extends BaseService {

//    public ResponseEntity<?> updateFlight(FlightUpdateRequestDTO flightUpdateRequestDTO) {
//
//    }
//
//    private FlightEntity getFlightEntity(Integer flightId) {
//
//    }
}
