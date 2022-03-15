package com.outsource.bookingticket.services;

import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight.FlightLog;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.MessageUtil;
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
        // Kiểm tra tồn tại thông tin chuyến bay, nếu không tồn tại trả về thông báo lỗi
        if (flightEntity.isEmpty()) {
            throw new ErrorException(MessageUtil.FLIGHT_NOT_FOUND_EX);
        }
        // Lấy thông tin ID của chuyến bay
        Integer flightId = flightEntity.get().getFlightId();

        // Lấy ID của user từ token và tìm user info theo ID
        Integer userId = jwtTokenProvider.getUserIdFromJWT(getTokenFromHeader(token));
        UserEntity user = userRepository.getById(userId);
        // Khởi tạo object để lưu log
        FlightLog newFlightLog = FlightLog.builder()
                .logDate(LocalDateTime.now())
                .username(user.getUsername())
                .flightId(flightId)
                .flightNoOld(oldValue.getFlightNo())
                .flightNoNew(newValue.getFlightNo())
                .build();
        // Gọi đến hàm lưu log
        flightLogRepository.save(newFlightLog);
    }

    private String getTokenFromHeader(String tokenHeader) {
        return tokenHeader.substring(7);
    }
}
