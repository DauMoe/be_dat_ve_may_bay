package com.outsource.bookingticket.services;

import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight.FlightLog;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class LogService extends BaseService {

    public void saveLogAfterUpdate(FlightSchedule oldValue, FlightSchedule newValue, String token) {
        // Tìm kiếm Flight theo new FlightSchedule để lưu xuống log
        Optional<FlightEntity> flightEntity = flightRepository.findFlightEntityByFlightNo(newValue.getFlightNo());
        Integer flightId = flightEntity.isPresent() ? flightEntity.get().getFlightId() : 0;
        // Khởi tạo object để lưu log
        FlightLog newFlightLog = FlightLog.builder()
                .logDate(LocalDateTime.now())
                .username(token)
                .flightId(flightId)
                .flightNoOld(oldValue.getFlightNo())
                .flightNoNew(newValue.getFlightNo())
                .build();
        // Gọi đến hàm lưu log
        flightLogRepository.save(newFlightLog);
    }
}
