package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.CreateScheduleResponseDTO;
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

    // Lấy hết lịch trình bay
    public ResponseEntity<?> getAllFlightSchedule() {
        List<FlightSchedule> flightScheduleList = flightScheduleRepository.findAll();
        return ResponseEntity.ok(Helper.createSuccessListCommon(Collections.singletonList(flightScheduleList)));
    }

    // API thêm lịch trình bay
    public ResponseEntity<?> addFlightSchedule(FlightRequestDTO flightRequestDTO) {
        // Kiểm tra thông tin đầu vào
        validateFlightSchedule(flightRequestDTO);
        // Tìm kiếm thông tin máy bay theo ID máy bay, nếu không có sẽ trả về lỗi
        Airplane airplane = airplaneRepository.findById(flightRequestDTO.getAirplaneId())
                .orElseThrow(() -> new ErrorException(MessageUtil.AIRPLANE_IS_NOT_EXIST));
        // Khởi tạo đối tượng FlightEntity và gán giá trị để lưu vào DB
        FlightEntity newFlight = FlightEntity.builder()
                .flightNo(flightRequestDTO.getFlightNo())
                .fromAirportId(flightRequestDTO.getFromAirportId())
                .toAirportId(flightRequestDTO.getToAirportId())
                .airplaneId(flightRequestDTO.getAirplaneId())
                .build();
        // Khởi tạo đối tượng FlightSchedule và gán giá trị để lưu vào DB
        FlightSchedule newSchedule = FlightSchedule.builder()
                .startTime(convertStringToLocalDateTime(flightRequestDTO.getStartTime()))
                .endTime(convertStringToLocalDateTime(flightRequestDTO.getEndTime()))
                .flightNo(flightRequestDTO.getFlightNo())
                .availableSeat(airplane.getCapacity())
                .flightState(FLIGHTSTATE.FLIGHT_ON)
                .build();
        // Gọi hàm lưu vào DB
        newFlight = flightRepository.save(newFlight);
        newSchedule = flightScheduleRepository.save(newSchedule);

        // Trả về thông báo thêm thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(createScheduleResponseDTO(newFlight, newSchedule)));
    }

    // Kiểm tra thông tin đầu vào xem lịch trình bay muốn thêm có trùng với lịch trình của cùng 1 máy bay trước đó không
    private void validateFlightSchedule(FlightRequestDTO flightRequestDTO) {
        // Tìm kiếm xem máy bay có chuyến bay nào chưa
        List<FlightEntity> flightEntityList = flightRepository.findAllByAirplaneId(flightRequestDTO.getAirplaneId());

        if (!CollectionUtils.isEmpty(flightEntityList)) {
            // Lấy danh sách mã chuyến bay
            List<String> flightNoList = flightEntityList
                                        .stream()
                                        .map(FlightEntity::getFlightNo)
                                        .collect(Collectors.toList());
            // Tìm hết lịch trình bay của máy bay đó
            List<FlightSchedule> flightScheduleList = flightScheduleRepository
                    .findAllByFlightNoInAndFlightState(flightNoList, FLIGHTSTATE.FLIGHT_ON);
            // Chuyển đổi thời gian để so sánh
            LocalDateTime startTimeConverted = convertStringToLocalDateTime(flightRequestDTO.getStartTime());
            LocalDateTime endTimeConverted = convertStringToLocalDateTime(flightRequestDTO.getEndTime());

            // Lọc ra những lịch trình thoả mãn điều kiện
            List<FlightSchedule> filterScheduleList = flightScheduleList
                                                    .stream()
                                                    .filter(p -> filterByDate(p, startTimeConverted, endTimeConverted))
                                                    .collect(Collectors.toList());
            // Trả về thông báo lỗi nếu như có lịch trình không thoả mãn điều kiện
            if (flightScheduleList.size() != filterScheduleList.size()) {
                throw new ErrorException(MessageUtil.DATETIME_ERROR);
            }
        }
    }

    // Kiểm tra thời gian thêm vào không được trùng với thời gian nào đã được thêm trước đó
    private boolean filterByDate(FlightSchedule flightSchedule, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime scheduleStart = flightSchedule.getStartTime();
        LocalDateTime scheduleEnd = flightSchedule.getEndTime();
        return startTime.isBefore(endTime) && (endTime.isBefore(scheduleStart) || startTime.isAfter(scheduleEnd));
    }

    private CreateScheduleResponseDTO createScheduleResponseDTO(FlightEntity flightEntity, FlightSchedule flightSchedule) {
        CreateScheduleResponseDTO responseDTO = new CreateScheduleResponseDTO();
        CreateScheduleResponseDTO.ScheduleResponseDTO scheduleResponseDTO = new CreateScheduleResponseDTO.ScheduleResponseDTO();

        scheduleResponseDTO.setFlightId(flightEntity.getFlightId());
        scheduleResponseDTO.setFlightNo(flightSchedule.getFlightNo());
        scheduleResponseDTO.setFlightScheduleId(flightSchedule.getFlightScheduleId());
        scheduleResponseDTO.setStartTime(convertLocalDatetimeToHourString(flightSchedule.getStartTime()));
        scheduleResponseDTO.setEndTime(convertLocalDatetimeToHourString(flightSchedule.getEndTime()));

        responseDTO.setFlightScheduleResponseDTO(scheduleResponseDTO);
        responseDTO.setMessage(MessageUtil.INSERT_SUCCESS);

        return responseDTO;
    }
}
