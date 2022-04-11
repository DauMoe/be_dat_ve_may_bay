package com.outsource.bookingticket.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.outsource.bookingticket.dtos.*;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.entities.common.FlightCommon;
import com.outsource.bookingticket.entities.common.TicketCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class TicketService extends BaseService {
    @Autowired
    private LogService logService;

    // Hàm thay đổi chuyến bay
    public ResponseEntity<?> updateFlight(FlightUpdateRequestDTO flightUpdateRequestDTO, String token) {
        // Lấy chuyến bay theo ID của chuyến bay
        Ticket ticket = getTicket(flightUpdateRequestDTO.getTicketId());

        // Tìm kiếm lịch trình bay cũ
        FlightSchedule flightScheduleOld = getFlightSchedule(ticket.getFlightScheduleId());

        // Tìm kiếm lịch trình bay mới
        FlightSchedule flightScheduleNew = getFlightSchedule(flightUpdateRequestDTO.getFlightScheduleId());
        // Thay đổi mã chuyến bay
        ticket.setFlightScheduleId(flightScheduleNew.getFlightScheduleId());

        // Gọi tới hàm thay đổi để thay đổi dưới DB
        ticketRepository.saveAndFlush(ticket);

        // Gọi tới hàm lưu log sau khi thay đổi
        logService.saveLogAfterUpdate(flightScheduleOld, flightScheduleNew, token);

        // Trả về thông báo thay đổi thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.FLIGHT_UPDATED_SUCCESS));
    }

    // Hàm lấy ra danh sách tất cả vé của user
    public ResponseEntity<?> getTickets(String token, String filter) {
        // Lấy ID của user từ token
        Integer userId = jwtTokenProvider.getUserIdFromJWT(getTokenFromHeader(token));

        // Lấy ra danh sách vé của user theo ID của user
        List<Ticket> tickets = ticketRepository.findByUid(userId);
        // Kiểm tra danh sách vé đó có rỗng hay không? nếu rỗng trả về lỗi
        if (!CollectionUtils.isEmpty(tickets)) {
            // khởi tạo 1 list để lưu thông tin vé
            List<Ticket> ticketList;
            // Kiểm tra filter truyền vào từ tham số có null hay không? nếu null thì gán ticketList = tickets
            if (Objects.nonNull(filter)) {
                // filter tickets theo tham số truyền vào ( BOOKED hoặc CANCELED)
                ticketList = tickets.stream()
                        .filter(t -> t.getBookingState().name().equals(filter.toUpperCase()))
                        .collect(Collectors.toList());
            } else ticketList = tickets;
            // Trả về response cho phía client
            return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(ticketList)));
        } else throw new ErrorException(MessageUtil.NOT_HAVE_ANY_TICKET);
    }

    // Hàm lấy ra thông tin chi tiết của 1 vé theo ID của vé
    public ResponseEntity<?> getDetailTicket(Integer ticketId) {
        // Tìm kiếm vé theo ID của vé
        Optional<Ticket> ticket = ticketRepository.findTicketByTicketId(ticketId);

        // Kiểm tra nếu vé tồn tại thì trả về response cho phía client. Nếu không tồn tại thì trả về lỗi
        if (ticket.isPresent()) {
            // Tìm người đặt mua vé
            Optional<UserEntity> user = userRepository.findUserEntityById(ticket.get().getUid());
            // Tìm kiếm lịch trình chuyến bay
            Optional<FlightSchedule> flightSchedule =
                    flightScheduleRepository.findFlightSchedulesByFlightScheduleId(ticket.get().getFlightScheduleId());
            // Kiểm tra tồn tại thông tin người dùng và lịch trình chuyến bay
            if (user.isPresent() && flightSchedule.isPresent()) {
                // Tìm kiếm thông tin chuyến bay
                Optional<FlightEntity> flightEntity =
                        flightRepository.findFlightEntityByFlightNo(flightSchedule.get().getFlightNo());
                List<FlightEntity> flightList = Collections.singletonList(flightEntity.get());
                // Lấy hết địa điểm bay
                List<Location> locationList = getAllLocationByFlight(flightList);
                // Lấy ra thông tin địa điểm đến
                Location locationTo = filterLocation(locationList, flightEntity.get().getToAirportId());
                // Lấy ra thông tin địa điểm xuất phát
                Location locationFrom = filterLocation(locationList, flightEntity.get().getFromAirportId());
                // Tìm kiếm tên máy bay
                Optional<Airplane> airplane = airplaneRepository.findById(flightEntity.get().getAirplaneId());

                // Gán thông tin trả về
                TicketDTO ticketDTO = mapToTicketDTO(ticket.get(), user.get(), flightSchedule.get(), flightEntity.get(),
                        locationTo, locationFrom, airplane.get());
                return ResponseEntity.ok(Helper.createSuccessCommon(ticketDTO));
            }
            // Trả về thông tin lỗi nếu có lỗi xảy ra
            throw new ErrorException(MessageUtil.SOME_ERRORS);
        } else throw new ErrorException(MessageUtil.TICKET_NOT_FOUND);
    }

    public ResponseEntity<?> getAllTicketByScheduleId(Integer scheduleId){
        List<TicketCommon> ticketCommons = ticketCommonRepository.findTicketByFlightScheduleId(scheduleId);
        List<TicketResponseDTO> ticketResponseDTOS =
        ticketCommons.stream().map(f ->
           mapToTicketCommon(f)
        ).collect(Collectors.toList());

        return  ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(ticketResponseDTOS)));
    }

    // Hàm lọc Location theo ID
    private Location filterLocation(List<Location> locationList, Integer locationId) {
        return locationList.stream().filter(l -> l.getLocationId().equals(locationId)).findFirst().orElse(null);
    }

    private TicketResponseDTO mapToTicketCommon(TicketCommon ticketCommon){
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setTicketId(ticketCommon.getTicketId());
        dto.setSeatNumber(ticketCommon.getSeatNumber());
        dto.setPrice(ticketCommon.getPrice());
        dto.setBookingState(ticketCommon.getBookingState());
        dto.setAirplaneName(ticketCommon.getAirplaneName());
        dto.setStartTime(convertLocalDatetimeToString(ticketCommon.getStartTime()));
        dto.setEndTime(convertLocalDatetimeToString(ticketCommon.getEndTime()));
        dto.setAvailableSeat(ticketCommon.getAvailableSeat());
        dto.setFlightNo(ticketCommon.getFlightNo());
        dto.setUsername(ticketCommon.getUsername());

        // Tìm kiếm lịch trình chuyến bay
        Optional<FlightSchedule> flightSchedule =
                flightScheduleRepository.findFlightSchedulesByFlightScheduleId(ticketCommon.getFlightScheduleId());
        // Tìm kiếm thông tin chuyến bay
        Optional<FlightEntity> flightEntity =
                flightRepository.findFlightEntityByFlightNo(flightSchedule.get().getFlightNo());
        List<FlightEntity> flightList = Collections.singletonList(flightEntity.get());
        // Lấy hết địa điểm bay
        List<Location> locationList = getAllLocationByFlight(flightList);
        // Lấy ra thông tin địa điểm đến
        Location locationTo = filterLocation(locationList, flightEntity.get().getToAirportId());
        // Lấy ra thông tin địa điểm xuất phát
        Location locationFrom = filterLocation(locationList, flightEntity.get().getFromAirportId());
        // Tìm kiếm tên máy bay
        Optional<Airplane> airplane = airplaneRepository.findById(flightEntity.get().getAirplaneId());

        dto.setFromAirport(locationFrom);
        dto.setToAirport(locationTo);

        return dto;
    }

    public ResponseCommon cancelTicket(Integer ticketId){

        // Hàm tìm thông tin vé theo Id của vé
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        // Kiểm tra vé tìm được có phải rỗng hay không? nếu rỗng trả về lỗi.
        if (ticket.isEmpty()) throw new ErrorException("Không Tìm Thấy Vé");
        // Kiểm tra vé đó đã bị hủy từ trước hay chưa? nếu đã bị hủy từ trước rồi thì trả về lỗi
        if (ticket.get().getBookingState().equals(BOOKINGSTATE.CANCELED)) throw new ErrorException("Vé Đã Bị Hủy");
        // Thay đổi trạng thái vé từ BOOKED sang CANCELED
        ticket.get().setBookingState(BOOKINGSTATE.CANCELED);
        // Cập nhật thông tin vé vào database
        ticketRepository.saveAndFlush(ticket.get());

        // Trả về các thông tin cho phía client.
        ResponseCommon response = new ResponseCommon();
        response.setCode(200);
        response.setResult("Hủy Vé Thành Công");

        return response;
    }

    private TicketDTO mapToTicketDTO(Ticket ticket, UserEntity user, FlightSchedule flightSchedule,
                                     FlightEntity flightEntity, Location locationTo, Location locationFrom,
                                     Airplane airplane) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setSeatNumber(ticket.getSeatNumber());
        ticketDTO.setPrice(ticket.getPrice());
        ticketDTO.setBookingState(ticket.getBookingState().name());
        ticketDTO.setAirplaneName(airplane.getAirplaneName());
        ticketDTO.setFlightSchedule(convertFlightScheduleToDTO(flightSchedule));
        ticketDTO.setFlightDTO(convertFlightEntityToDTO(flightEntity, locationTo, locationFrom));
        ticketDTO.setUserDetailDTO(mapUserToUserDetailDTO(user));
        return ticketDTO;
    }

    // Hàm convert FlightSchedule sang FlightScheduleResponseDTO
    private TicketDTO.FlightScheduleDTO convertFlightScheduleToDTO(FlightSchedule flightSchedule) {
        TicketDTO.FlightScheduleDTO responseDTO = new TicketDTO.FlightScheduleDTO();
        responseDTO.setStartTime(convertLocalDatetimeToString(flightSchedule.getStartTime()));
        responseDTO.setEndTime(convertLocalDatetimeToString(flightSchedule.getEndTime()));
        responseDTO.setAvailableSeat(flightSchedule.getAvailableSeat());

        return responseDTO;
    }

    // Hàm convert FlightEntity sang FlightResponseDTO
    private FlightResponseDTO convertFlightEntityToDTO(FlightEntity flightEntity, Location locationTo, Location locationFrom) {
        FlightResponseDTO responseDTO = new FlightResponseDTO();
        responseDTO.setFlightNo(flightEntity.getFlightNo());
        responseDTO.setFlightId(flightEntity.getFlightId());
        responseDTO.setAirplaneId(flightEntity.getAirplaneId());
        responseDTO.setFromAirport(mapLocation(locationFrom));
        responseDTO.setToAirport(mapLocation(locationTo));
        return responseDTO;
    }

    // Hàm chuyển Location sang LocationDTO để trả về
    private LocationDTO mapLocation(Location location) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocationId(location.getLocationId());
        locationDTO.setCountry(Objects.nonNull(location.getCountryName()) ? location.getCountryName() : "");
        locationDTO.setCity(Objects.nonNull(location.getCityName()) ? location.getCityName() : "");
        return locationDTO;
    }

    private UserDetailDTO mapUserToUserDetailDTO(UserEntity user) {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        userDetailDTO.setUsername(user.getUsername());
        userDetailDTO.setEmail(user.getEmail());
        userDetailDTO.setPhone(user.getPhone());
        return userDetailDTO;
    }

}
