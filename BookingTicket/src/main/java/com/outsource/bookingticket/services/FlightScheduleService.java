package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.*;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.entities.enums.FLIGHTSTATE;
import com.outsource.bookingticket.entities.enums.TICKETTYPE;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.flight_schedule.Schedule;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.entities.ticket.CountTicket;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightScheduleService extends BaseService {

    // Lấy hết lịch trình bay
    public ResponseEntity<?> getAllFlightSchedule(String startTime, String endTime, Integer from, Integer to) {
        // Khởi tạo danh sách chứa ID địa điểm để tìm kiếm
        Set<Integer> idLocationSet = new HashSet<>();
        // Khởi tạo danh sách chứa ID máy bay để tìm kiếm
        Set<Integer> idAirplaneSet = new HashSet<>();
        // Lấy danh sách lịch trình
        List<Schedule> schedulesList = getAllSchedule(startTime, endTime, from, to);

        if (CollectionUtils.isEmpty(schedulesList)) {
            throw new ErrorException(MessageUtil.FLIGHT_SCHEDULE_NOT_FOUND_EX);
        }

        schedulesList.forEach(s -> {
            idLocationSet.add(s.getFromAirportId());
            idLocationSet.add(s.getToAirportId());
            idAirplaneSet.add(s.getAirplaneId());
        });

        List<Location> locationList = locationRepository.findLocationsByLocationIdIn(idLocationSet);

        List<Airplane> airplaneList = airplaneRepository.findAllById(idAirplaneSet);

        List<CountTicket> countTicketList = countTicketRepository.findAllGroupBySchedule();

        List<ScheduleResponseDTO> responseDTOList = mapToScheduleResponseDTOs(schedulesList, locationList, countTicketList, airplaneList);

        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(responseDTOList)));
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

    // Hàm gán dữ liệu dạng danh sách để trả về
    private List<ScheduleResponseDTO> mapToScheduleResponseDTOs(List<Schedule> scheduleList, List<Location> locationList,
                                                                List<CountTicket> countTicketList, List<Airplane> airplaneList) {
        return scheduleList.stream()
                .map(s -> mapToScheduleResponseDTO(s, locationList, countTicketList, airplaneList))
                .collect(Collectors.toList());
    }

    // Hàm gán dữ liệu để trả về
    private ScheduleResponseDTO mapToScheduleResponseDTO(Schedule schedule, List<Location> locationList,
                                                         List<CountTicket> countTicketList, List<Airplane> airplaneList) {
        ScheduleResponseDTO responseDTO = new ScheduleResponseDTO();
        Location fromLocation = filterLocation(locationList, schedule.getFromAirportId());
        Location toLocation = filterLocation(locationList, schedule.getToAirportId());
        Airplane airplane = filterAirplane(airplaneList, schedule.getAirplaneId());

        responseDTO.setFlightScheduleId(schedule.getFlightScheduleId());
        responseDTO.setStartTime(convertLocalDatetimeToString(schedule.getStartTime()));
        responseDTO.setEndTime(convertLocalDatetimeToString(schedule.getEndTime()));
        responseDTO.setFlightNo(schedule.getFlightNo());
        responseDTO.setFromLocation(mapLocation(fromLocation));
        responseDTO.setToLocation(mapLocation(toLocation));
        responseDTO.setAirplaneDTO(mapToAirplaneDTO(airplane));
        responseDTO.setTotalTicketFirstClass(
                filterCountTicketByScheduleIdAndTicketType(countTicketList, schedule.getFlightScheduleId(), TICKETTYPE.FIRST_CLASS));
        responseDTO.setTotalTicketBusinessClass(
                filterCountTicketByScheduleIdAndTicketType(countTicketList, schedule.getFlightScheduleId(), TICKETTYPE.BUSINESS_CLASS)
        );
        responseDTO.setTotalTicketPremiumClass(
                filterCountTicketByScheduleIdAndTicketType(countTicketList, schedule.getFlightScheduleId(), TICKETTYPE.PREMIUM_CLASS)
        );
        responseDTO.setTotalTicketEconomyClass(
                filterCountTicketByScheduleIdAndTicketType(countTicketList, schedule.getFlightScheduleId(), TICKETTYPE.ECONOMY_CLASS)
        );

        return responseDTO;
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

    // Hàm lấy danh sách lịch trình
    private List<Schedule> getAllSchedule(String startTime, String endTime, Integer from, Integer to) {
        List<Schedule> schedulesList;

        // Kiểm tra nếu có thời gian truyền vào sẽ format đúng định dạng
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            startTime = formatDateTime(startTime, false);
            endTime = formatDateTime(endTime, true);
        }

        if (Objects.nonNull(startTime) && Objects.nonNull(endTime) && Objects.nonNull(from) && Objects.nonNull(to)) {
            // Tìm kiếm danh sách lịch trình theo thời gian và địa điểm
            schedulesList = scheduleRepository.findAllScheduleByTimeAndLocation(startTime, endTime, from, to);
        } else {
            // Tìm kiếm danh sách lịch trình theo thời gian
            if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
                schedulesList = scheduleRepository.findAllScheduleByTime(startTime, endTime);
            // Tìm kiếm danh sách lịch trình theo địa điểm
            } else if (Objects.nonNull(from) && Objects.nonNull(to)) {
                schedulesList = scheduleRepository.findAllScheduleByLocation(from, to);
            // Tìm kiếm tất cả danh sách địa điểm
            } else {
                schedulesList = scheduleRepository.findAllSchedule();
            }
        }
        return schedulesList;
    }

    // Kiểm tra thời gian thêm vào không được trùng với thời gian nào đã được thêm trước đó
    private boolean filterByDate(FlightSchedule flightSchedule, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime scheduleStart = flightSchedule.getStartTime();
        LocalDateTime scheduleEnd = flightSchedule.getEndTime();
        return startTime.isBefore(endTime) && (endTime.isBefore(scheduleStart) || startTime.isAfter(scheduleEnd));
    }

    // Gán dữ liệu để trả về
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

    // Hàm lọc thông tin chuyến bay trong danh sách chuyến bay theo ID máy bay
    private Airplane filterAirplane(List<Airplane> airplaneList, Integer airplaneId) {
        return airplaneList.stream().filter(l -> l.getAirplaneId().equals(airplaneId)).findFirst().orElse(null);
    }

    // Hàm lọc ra số vé theo ID lich trình bay và từng loại vé
    private Integer filterCountTicketByScheduleIdAndTicketType(List<CountTicket> countTicketList,
                                                               Integer scheduleId, TICKETTYPE tickettype) {
        Optional<CountTicket> countTicket = countTicketList.stream()
                .filter(ticket -> ticket.getFlightScheduleId().equals(scheduleId) && ticket.getTicketType() == tickettype)
                .findFirst();
        return countTicket.isPresent() ? countTicket.get().getTotalTicket() : 0;
    }
}
