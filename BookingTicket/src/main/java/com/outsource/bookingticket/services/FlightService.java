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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService extends BaseService {

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
        // Kiểm tra ID chuyến bay có rỗng không, nếu rỗng trả ra thông báo lỗi
        if (Objects.nonNull(flightId)) {
            // Tìm kiếm chuyến bay theo ID
            Optional<FlightEntity> flightEntity = flightRepository.findFlightEntityByFlightId(flightId);
            // Tồn tại thông tin chuyến bay
            if (flightEntity.isPresent()) {
                List<FlightSchedule> flightSchedulesFilter;
                // Tìm kiếm lịch trình chuyến bay theo mã chuyến bay
                List<FlightSchedule> flightSchedules =
                        flightScheduleRepository.findFlightSchedulesByFlightNo(flightEntity.get().getFlightNo());
                // Kiểm tra nếu không truyền vào tham số thời gian bắt đầu và kết thúc thì sẽ trả về hết dữ liệu
                if (Objects.isNull(startTimeStr) && Objects.isNull(endTimeStr)) {
                    flightSchedulesFilter = flightSchedules;
                } else {
                    // Kiểm tra nếu nhập thời gian cần nhập đủ cả 2
                    if (!(Objects.nonNull(startTimeStr) && Objects.nonNull(endTimeStr))) {
                        throw new ErrorException(MessageUtil.FILL_DATETIME);
                    }
                    // Format dạng date từ string sang LocalDatetime
                    LocalDateTime startTime = convertStringToLocalDateTime(startTimeStr);
                    LocalDateTime endTime = convertStringToLocalDateTime(endTimeStr);
                    // Lọc những FlightSchedule theo thời gian
                    flightSchedulesFilter = flightSchedules.stream()
                            .filter(i -> filterByDate(i, startTime, endTime))
                            .collect(Collectors.toList());
                }

                // Kiểm tra danh sách sau khi tìm kiếm theo ngày có dữ liệu hay không, nếu không có trả ra thông báo lỗi
                if (!CollectionUtils.isEmpty(flightSchedulesFilter)) {
                    // Convert danh sách FlightEntity sang danh sách FlightScheduleResponseDTO
                    List<FlightScheduleResponseDTO> listFlightResult = convertFlightScheduleToListDTO(flightSchedulesFilter);
                    // Trả về kết quả thành công
                    return ResponseEntity.ok(Helper.createSucessCommon(listFlightResult));
                } else throw new ErrorException(MessageUtil.NOT_HAVE_ANY_FLIGHT);
            }
        }
        // Trả về thông báo lỗi
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

        // Convert danh sách FlightEntity sang danh sách FlightScheduleResponseDTO
        List<FlightResponseDTO> listFlightResponse = convertFlightEntityToListDTO(listFlightResult);

        if (!CollectionUtils.isEmpty(listFlightResponse)) {
            // Trả về kết quả thành công
            return ResponseEntity.ok(Helper.createSucessCommon(listFlightResponse));
        } else throw new ErrorException(MessageUtil.NOT_HAVE_ANY_FLIGHT);
    }

    public ResponseEntity<?> getAllFlightSchedule() {
        List<FlightSchedule> flightSchedule = flightScheduleRepository.findAll();
        if (!CollectionUtils.isEmpty(flightSchedule)) {
            List<FlightScheduleResponseDTO> listScheduleResult = convertFlightScheduleToListDTO(flightSchedule);
            return ResponseEntity.ok(Helper.createSucessCommon(listScheduleResult));
        } else throw new ErrorException(MessageUtil.FLIGHT_SCHEDULE_IS_EMPTY);
    }

    private boolean filterByDate(FlightSchedule flightSchedule, LocalDateTime startDate, LocalDateTime endDate) {
        // Lọc những lịch trình chuyến bay nằm trong thời gian tìm kiếm
        return flightSchedule.getStartTime().toLocalDate().isEqual(startDate.toLocalDate()) &&
                flightSchedule.getEndTime().toLocalDate().isEqual(endDate.toLocalDate());
    }

    // Hàm tìm kiếm chuyến bay
    private List<FlightEntity> searchListFlight(Integer fromAirportId, Integer toAirportId, String flightNo) {
        // Khởi tạo đối tương để tạo câu truy vấn
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FlightEntity> flightEntityQuery = builder.createQuery(FlightEntity.class);

        // Khởi tạo danh sách điều kiện
        Root<FlightEntity> flightEntityRoot = flightEntityQuery.from(FlightEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        // Kiểm tra nếu fromAirportId khác rỗng sẽ thêm điều kiện tìm kiếm theo field này vào câu truy vấn
        if (Objects.nonNull(fromAirportId)) {
            predicates.add(builder.equal(flightEntityRoot.get("fromAirportId"), fromAirportId));
        }

        // Kiểm tra nếu toAirportId khác rỗng sẽ thêm điều kiện tìm kiếm theo field này vào câu truy vấn
        if (Objects.nonNull(toAirportId)) {
            predicates.add(builder.equal(flightEntityRoot.get("toAirportId"), toAirportId));
        }

        // Kiểm tra nếu flightNo khác rỗng sẽ thêm điều kiện tìm kiếm theo field này vào câu truy vấn
        if (Objects.nonNull(flightNo)) {
            predicates.add(builder.equal(flightEntityRoot.get("flightNo"), flightNo));
        }

        // Thực hiện gán danh sách điều kiện truy vấn
        flightEntityQuery.where(predicates.toArray(new Predicate[0]));
        // Thực hiện câu query vừa ghép ở trên để chạy và trả về kết quả
        return entityManager.createQuery(flightEntityQuery).getResultList();
    }

    // Hàm convert FlightEntity sang FlightResponseDTO
    private FlightResponseDTO convertFlightEntityToDTO(FlightEntity flightEntity) {
        FlightResponseDTO responseDTO = new FlightResponseDTO();
        responseDTO.setFlightNo(flightEntity.getFlightNo());
        responseDTO.setFlightId(flightEntity.getFlightId());
        responseDTO.setAirplaneId(flightEntity.getAirplaneId());
        responseDTO.setFromAirportId(flightEntity.getFromAirportId());
        responseDTO.setToAirportId(flightEntity.getToAirportId());
        return responseDTO;
    }

    // Hàm convert danh sách FlightEntity sang danh sách FlightResponseDTO
    private List<FlightResponseDTO> convertFlightEntityToListDTO(List<FlightEntity> listFlightEntity) {
        return listFlightEntity.stream().map(this::convertFlightEntityToDTO).collect(Collectors.toList());
    }

    // Hàm convert FlightSchedule sang FlightScheduleResponseDTO
    private FlightScheduleResponseDTO convertFlightScheduleToDTO(FlightSchedule flightSchedule) {
        FlightScheduleResponseDTO responseDTO = new FlightScheduleResponseDTO();
        responseDTO.setFlightScheduleId(flightSchedule.getFlightScheduleId());
        responseDTO.setStartTime(convertLocalDatetimeToString(flightSchedule.getStartTime()));
        responseDTO.setEndTime(convertLocalDatetimeToString(flightSchedule.getEndTime()));
        responseDTO.setAvailableSeat(flightSchedule.getAvailableSeat());
        responseDTO.setFlightNo(flightSchedule.getFlightNo());

        return responseDTO;
    }

    // Hàm convert danh sách FlightSchedule sang danh sách FlightScheduleResponseDTO
    private List<FlightScheduleResponseDTO> convertFlightScheduleToListDTO(List<FlightSchedule> listFlightSchedule) {
        return listFlightSchedule.stream().map(this::convertFlightScheduleToDTO).collect(Collectors.toList());
    }
}
