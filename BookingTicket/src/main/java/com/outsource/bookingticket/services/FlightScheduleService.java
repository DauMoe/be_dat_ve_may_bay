package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightRequestDTO;
import com.outsource.bookingticket.dtos.LocationRequestDTO;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.entities.enums.FLIGHTSTATE;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightScheduleService extends BaseService {

    public ResponseEntity<?> getAllFlightSchedule() {
        List<FlightSchedule> flightScheduleList = flightScheduleRepository.findAll();
        return ResponseEntity.ok(Helper.createSuccessListCommon(Collections.singletonList(flightScheduleList)));
    }

    // API thêm lịch trình bay
    public ResponseEntity<?> addFlightSchedule(FlightRequestDTO flightRequestDTO) {
        validateFlightSchedule(flightRequestDTO);
        Airplane airplane = airplaneRepository.findById(flightRequestDTO.getAirplaneId())
                .orElseThrow(() -> new ErrorException(MessageUtil.AIRPLANE_IS_NOT_EXIST));
        FlightEntity newFlight = FlightEntity.builder()
                .flightNo(flightRequestDTO.getFlightNo())
                .fromAirportId(flightRequestDTO.getFromAirportId())
                .toAirportId(flightRequestDTO.getToAirportId())
                .airplaneId(flightRequestDTO.getAirplaneId())
                .build();
        FlightSchedule newSchedule = FlightSchedule.builder()
                .startTime(convertStringToLocalDateTime(flightRequestDTO.getStartTime()))
                .endTime(convertStringToLocalDateTime(flightRequestDTO.getEndTime()))
                .flightNo(flightRequestDTO.getFlightNo())
                .availableSeat(airplane.getCapacity())
                .flightState(FLIGHTSTATE.FLIGHT_ON)
                .build();
        flightRepository.save(newFlight);
        flightScheduleRepository.save(newSchedule);

        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.INSERT_SUCCESS));
    }

    private void validateFlightSchedule(FlightRequestDTO flightRequestDTO) {
        List<FlightEntity> flightEntityList = flightRepository.findAllByAirplaneId(flightRequestDTO.getAirplaneId());

        if (!CollectionUtils.isEmpty(flightEntityList)) {
            List<String> flightNoList = flightEntityList
                                        .stream()
                                        .map(FlightEntity::getFlightNo)
                                        .collect(Collectors.toList());
            List<FlightSchedule> flightScheduleList = flightScheduleRepository
                    .findAllByFlightNoInAndFlightState(flightNoList, FLIGHTSTATE.FLIGHT_ON);
            LocalDateTime startTimeConverted = convertStringToLocalDateTime(flightRequestDTO.getStartTime());
            LocalDateTime endTimeConverted = convertStringToLocalDateTime(flightRequestDTO.getEndTime());
            List<FlightSchedule> filterScheduleList = flightScheduleList
                                                    .stream()
                                                    .filter(p -> filterByDate(p, startTimeConverted, endTimeConverted))
                                                    .collect(Collectors.toList());
            if (flightScheduleList.size() != filterScheduleList.size()) {
                throw new ErrorException(MessageUtil.DATETIME_ERROR);
            }
        }
    }

    private boolean filterByDate(FlightSchedule flightSchedule, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime scheduleStart = flightSchedule.getStartTime();
        LocalDateTime scheduleEnd = flightSchedule.getEndTime();
        return startTime.isBefore(endTime) && (endTime.isBefore(scheduleStart) || startTime.isAfter(scheduleEnd));
    }

}
