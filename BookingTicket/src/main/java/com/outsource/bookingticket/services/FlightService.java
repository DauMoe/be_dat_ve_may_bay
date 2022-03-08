package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService extends BaseService {

    @Autowired private LogService logService;

    public ResponseEntity<?> updateFlight(FlightUpdateRequestDTO flightUpdateRequestDTO, String token) {
        FlightEntity flightEntity = getFlightEntity(flightUpdateRequestDTO.getFlightId());
        if (Objects.nonNull(flightUpdateRequestDTO.getFlightNo())) {
            flightEntity.setFlightNo(flightUpdateRequestDTO.getFlightNo());

            FlightEntity updated = flightRepository.saveAndFlush(flightEntity);
            logService.saveLogAfterUpdate(flightEntity, updated, token);
            return ResponseEntity.ok(Helper.createSucessCommon(MessageUtil.FLIGHT_UPDATED_SUCCESS));
        }
        throw new ErrorException(MessageUtil.FLIGHT_UPDATED_EX);
    }

//    public ResponseEntity<?> getListFlightByTime() {
//        List<FlightEntity> listFlight = flightRepository.findAll();
//        LinkedList<FlightEntity> listFlightResult = listFlight.stream()
//                .filter()
//                .collect(Collectors.toList());
//    }

    private FlightEntity getFlightEntity(Integer flightId) {
        if (Objects.nonNull(flightId)) {
            Optional<FlightEntity> flightEntityOp = flightRepository.findFlightEntityByFlightId(flightId);
            if (flightEntityOp.isPresent()) {
                return flightEntityOp.get();
            }
        }
        throw new ErrorException(MessageUtil.FLIGHT_NOT_FOUND_EX);
    }
}
