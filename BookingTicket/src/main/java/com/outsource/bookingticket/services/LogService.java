package com.outsource.bookingticket.services;

import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight.FlightLog;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class LogService extends BaseService {

    public void saveLogAfterUpdate(FlightEntity oldValue, FlightEntity newValue, String token) {
        // Khởi tạo object để lưu log
        FlightLog newFlightLog = FlightLog.builder()
                .logDate(LocalDateTime.now())
                .username(token)
                .flightId(oldValue.getFlightId())
                .flightNoOld(oldValue.getFlightNo())
                .flightNoNew(newValue.getFlightNo())
                .build();
        // Gọi đến hàm lưu log
        flightLogRepository.save(newFlightLog);
    }
}
