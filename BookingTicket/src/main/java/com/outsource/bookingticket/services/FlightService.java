package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightResponseDTO;
import com.outsource.bookingticket.dtos.FlightScheduleResponseDTO;
import com.outsource.bookingticket.dtos.FlightUpdateRequestDTO;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService extends BaseService {

    @Autowired
    private LogService logService;

    // Hàm thay đổi chuyến bay
    public ResponseEntity<?> updateFlight(FlightUpdateRequestDTO flightUpdateRequestDTO, String token) {
        // Lấy chuyến bay theo ID của chuyến bay
        FlightEntity flightEntity = getFlightEntity(flightUpdateRequestDTO.getFlightId());
        // Nếu như có chuyến bay sẽ xử lý thay đổi
        if (Objects.nonNull(flightUpdateRequestDTO.getFlightNo())) {
            // Thay đổi mã chuyến bay
            flightEntity.setFlightNo(flightUpdateRequestDTO.getFlightNo());

            // Gọi tới hàm thay đổi để thay đổi dưới DB
            FlightEntity updated = flightRepository.saveAndFlush(flightEntity);
            // Gọi tới hàm lưu log sau khi thay đổi
            logService.saveLogAfterUpdate(flightEntity, updated, token);
            // Trả về thông báo thay đổi thành công
            return ResponseEntity.ok(Helper.createSucessCommon(MessageUtil.FLIGHT_UPDATED_SUCCESS));
        }
        // Báo lỗi nếu như không tồn tại ID chuyến bay
        throw new ErrorException(MessageUtil.FLIGHT_UPDATED_EX);
    }

    // Hàm lấy thông tin chuyến bay theo thời gian
    public ResponseEntity<?> getListFlight() {
        // Lấy hết thông tin chuyến bay
        List<FlightEntity> listFlight = flightRepository.findAll();

        // Kiểm tra danh sách chuyến bay có trống không
        if (!CollectionUtils.isEmpty(listFlight)) {
            // Convert list entity sang list entity DTO để trả về
            List<FlightResponseDTO> listFlightResult = convertFlightEntityToListDTO(listFlight);
            // Trả về dữ liệu thành công
            return ResponseEntity.ok(Helper.createSucessCommon(listFlightResult));
        }
        // Không có chuyến bay nào sẽ trả về thông báo
        else throw new ErrorException(MessageUtil.FLIGHT_IS_EMPTY);
    }

    // Hàm lấy thông tin chuyến bay theo ID chuyến bay
    public ResponseEntity<?> getDetailFlight(Integer flightId, String startTimeStr, String endTimeStr) {
        if (Objects.nonNull(flightId)) {
            Optional<FlightEntity> flightEntity = flightRepository.findFlightEntityByFlightId(flightId);
            if (flightEntity.isPresent()) {
                List<FlightSchedule> flightSchedulesFilter;
                List<FlightSchedule> flightSchedules =
                        flightScheduleRepository.findFlightSchedulesByFlightNo(flightEntity.get().getFlightNo());
                if (Objects.isNull(startTimeStr) && Objects.isNull(endTimeStr)) {
                    flightSchedulesFilter = flightSchedules;
                } else {
                    LocalDateTime startTime = convertStringToLocalDateTime(startTimeStr);
                    LocalDateTime endTime = convertStringToLocalDateTime(endTimeStr);
                    flightSchedulesFilter = flightSchedules.stream()
                            .filter(i -> filterByDate(i, startTime, endTime))
                            .collect(Collectors.toList());
                }

                List<FlightScheduleResponseDTO> listFlightResult = convertFlightScheduleToListDTO(flightSchedulesFilter);
                return ResponseEntity.ok(Helper.createSucessCommon(listFlightResult));
            }
        }
        throw new ErrorException(MessageUtil.FLIGHT_NOT_FOUND_EX);
    }

    // Hàm tìm kiếm chuyến bay theo vị trí khởi hành, kết thúc hoặc mã chuyến bay
    public ResponseEntity<?> searchFlight(Integer fromAirportId, Integer toAirportId, String flightNo) {
        // Kiểm tra nếu các biến truyền vào đều null thì sẽ trả ra thông báo lỗi
        if (Objects.isNull(fromAirportId) && Objects.isNull(toAirportId) && Objects.isNull(flightNo)) {
            throw new ErrorException(MessageUtil.FILL_TO_SEARCH);
        }

        // Gọi tới hàm tìm kiếm theo vị trí khởi hành, kết thúc hoặc mã chuyến bay
        List<FlightEntity> listFlightResult = searchListFlight(fromAirportId, toAirportId, flightNo);
        List<FlightResponseDTO> listFlightResponse = convertFlightEntityToListDTO(listFlightResult);

        return ResponseEntity.ok(Helper.createSucessCommon(listFlightResponse));
    }

    // Hàm lấy thông tin chuyến bay
    private FlightEntity getFlightEntity(Integer flightId) {
        // Kiểm tra ID chuyến bay không rỗng sẽ tìm kiếm; nếu rỗng sẽ trả ra thông báo lỗi
        if (Objects.nonNull(flightId)) {
            // Tìm thông tin chuyến bay theo flightId
            Optional<FlightEntity> flightEntityOp = flightRepository.findFlightEntityByFlightId(flightId);
            // Kiểm tra dữ liệu có tồn tại
            if (flightEntityOp.isPresent()) {
                // Trả về thông tin chuyến bay
                return flightEntityOp.get();
            }
        }
        throw new ErrorException(MessageUtil.FLIGHT_NOT_FOUND_EX);
    }

    private boolean filterByDate(FlightSchedule flightSchedule, LocalDateTime startDate, LocalDateTime endDate) {
        return (flightSchedule.getStartTime().isEqual(startDate) || flightSchedule.getStartTime().isAfter(startDate)) &&
                (flightSchedule.getEndTime().isEqual(endDate) || flightSchedule.getEndTime().isBefore(endDate));
    }

    private LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (Exception e) {
            throw new ErrorException(MessageUtil.DATETIME_ERROR);
        }
    }

    private List<FlightEntity> searchListFlight(Integer fromAirportId, Integer toAirportId, String flightNo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FlightEntity> flightEntityQuery = builder.createQuery(FlightEntity.class);

        Root<FlightEntity> flightEntityRoot = flightEntityQuery.from(FlightEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(fromAirportId)) {
            predicates.add(builder.equal(flightEntityRoot.get("from_airport_id"), fromAirportId));
        }

        if (Objects.nonNull(toAirportId)) {
            predicates.add(builder.equal(flightEntityRoot.get("to_airport_id"), toAirportId));
        }

        if (Objects.nonNull(flightNo)) {
            predicates.add(builder.equal(flightEntityRoot.get("flight_no"), flightNo));
        }

        flightEntityQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(flightEntityQuery).getResultList();
    }

    // Hàm convert FlightEntity sang FlightResponseDTO
    private FlightResponseDTO convertFlightEntityToDTO(FlightEntity flightEntity) {
        FlightResponseDTO responseDTO = new FlightResponseDTO();
        responseDTO.setFlightNo(flightEntity.getFlightNo());
        responseDTO.setFlightId(flightEntity.getFlightId());
        responseDTO.setAirplaneId(responseDTO.getAirplaneId());
        responseDTO.setFromAirportId(responseDTO.getFromAirportId());
        responseDTO.setToAirportId(responseDTO.getToAirportId());
        return responseDTO;
    }

    private List<FlightResponseDTO> convertFlightEntityToListDTO(List<FlightEntity> listFlightEntity) {
        return listFlightEntity.stream().map(this::convertFlightEntityToDTO).collect(Collectors.toList());
    }

    private FlightScheduleResponseDTO convertFlightScheduleToDTO(FlightSchedule flightSchedule) {
        FlightScheduleResponseDTO responseDTO = new FlightScheduleResponseDTO();
        responseDTO.setFlightScheduleId(flightSchedule.getFlightScheduleId());
        responseDTO.setStartTime(flightSchedule.getStartTime());
        responseDTO.setEndTime(flightSchedule.getEndTime());
        responseDTO.setAvailableSeat(flightSchedule.getAvailableSeat());
        responseDTO.setFlightNo(flightSchedule.getFlightNo());

        return responseDTO;
    }

    private List<FlightScheduleResponseDTO> convertFlightScheduleToListDTO(List<FlightSchedule> listFlightSchedule) {
        return listFlightSchedule.stream().map(this::convertFlightScheduleToDTO).collect(Collectors.toList());
    }
}
