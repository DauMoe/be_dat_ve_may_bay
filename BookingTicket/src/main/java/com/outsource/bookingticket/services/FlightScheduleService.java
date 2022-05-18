package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.LocationRequestDTO;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class FlightScheduleService extends BaseService {

    public ResponseEntity<?> getAllFlightSchedule() {
        List<FlightSchedule> flightScheduleList = flightScheduleRepository.findAll();
        return ResponseEntity.ok(Helper.createSuccessListCommon(Collections.singletonList(flightScheduleList)));
    }
}
