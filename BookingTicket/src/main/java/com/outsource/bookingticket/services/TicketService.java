package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.*;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.entities.common.TicketCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.enums.TICKETTYPE;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class TicketService extends BaseService {

    // Hàm lấy ra thông tin chi tiết của 1 vé theo ID của vé
    public ResponseEntity<?> getDetailTicket(Integer ticketId) {
        // Tìm kiếm vé theo ID của vé
        Optional<Ticket> ticket = ticketRepository.findTicketByTicketId(ticketId);

        // Kiểm tra nếu vé tồn tại thì trả về response cho phía client. Nếu không tồn tại thì trả về lỗi
        if (ticket.isPresent()) {
            // Tìm kiếm lịch trình chuyến bay
            Optional<FlightSchedule> flightSchedule =
                    flightScheduleRepository.findFlightSchedulesByFlightScheduleId(ticket.get().getFlightScheduleId());
            // Kiểm tra tồn tại thông tin người dùng và lịch trình chuyến bay
            if (flightSchedule.isPresent()) {
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
                TicketDTO ticketDTO = mapToTicketDTO(ticket.get(), flightSchedule.get(), flightEntity.get(), locationTo,
                        locationFrom, airplane.get());
                return ResponseEntity.ok(Helper.createSuccessCommon(ticketDTO));
            }
            // Trả về thông tin lỗi nếu có lỗi xảy ra
            throw new ErrorException(MessageUtil.SOME_ERRORS);
        } else throw new ErrorException(MessageUtil.TICKET_NOT_FOUND);
    }

    // Hàm lấy toàn bộ thông tin vé của 1 schedule
    public ResponseEntity<?> getAllTicketByScheduleId(Integer scheduleId){
        // Tìm kiếm tất cả vé theo lịch trình bay
        List<TicketCommon> ticketCommons = ticketCommonRepository.findTicketByFlightScheduleId(scheduleId);
        // Hàm chuyển danh sách thông tin ticket sang danh sách TicketResponseDTO
        List<TicketResponseDTO> ticketResponseDTOS = ticketCommons.stream()
                .map(this::mapToTicketCommon)
                .collect(Collectors.toList());

        // Trả về dữ liệu cần tìm
        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(ticketResponseDTOS)));
    }

    // Hàm lọc Location theo ID
    private Location filterLocation(List<Location> locationList, Integer locationId) {
        return locationList.stream().filter(l -> l.getLocationId().equals(locationId)).findFirst().orElse(null);
    }

    // Chuyến dữ liệu từ TicketCommon sang TicketResponseDTO
    private TicketResponseDTO mapToTicketCommon(TicketCommon ticketCommon){
        // Khởi tạo đối tượng TicketResponseDTO
        TicketResponseDTO dto = new TicketResponseDTO();
        // Gán các giá trị thuộc tính
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

        dto.setFromAirport(locationFrom);
        dto.setToAirport(locationTo);

        // Trả về dữ liệu
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

    private TicketDTO mapToTicketDTO(Ticket ticket, FlightSchedule flightSchedule, FlightEntity flightEntity,
                                     Location locationTo, Location locationFrom, Airplane airplane) {
        Long tax = ticket.getPrice() * 50 / 100;
        Long totalPrice = ticket.getPrice() + tax;
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setSeatNumber(ticket.getSeatNumber());
        ticketDTO.setTicketType(ticket.getSeatNumber() + "_" + TICKETTYPE.getValue(ticket.getTicketType().name()).value);
        ticketDTO.setPrice(new TicketDTO.PriceDTO(ticket.getPrice(), tax, totalPrice));
        ticketDTO.setBookingState(ticket.getBookingState().name());
        ticketDTO.setAirplaneDTO(new AirplaneDTO(airplane.getAirplaneName(), airplane.getBrand(), airplane.getLinkImgBrand()));
        ticketDTO.setFlightSchedule(convertFlightScheduleToDTO(flightSchedule));
        ticketDTO.setFlightDTO(convertFlightEntityToDTO(flightEntity, locationTo, locationFrom));
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
    private TicketDTO.FlightDetailDTO convertFlightEntityToDTO(FlightEntity flightEntity, Location locationTo, Location locationFrom) {
        TicketDTO.FlightDetailDTO responseDTO = new TicketDTO.FlightDetailDTO();
        responseDTO.setFlightNo(flightEntity.getFlightNo());
        responseDTO.setFlightId(flightEntity.getFlightId());
        responseDTO.setToAirport(mapLocation(locationTo));
        responseDTO.setFromAirport(mapLocation(locationFrom));

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

}
