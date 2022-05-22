package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
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
import com.outsource.bookingticket.entities.users.Passenger;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class TicketService extends BaseService {

    // Hàm lấy ra thông tin chi tiết của 1 vé theo ID của vé
    public ResponseEntity<?> getDetailTicket(Integer ticketId, Integer totalAdult, Integer totalChildren, Integer totalBaby) {
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

                Passenger passenger = null;

                if (ticket.get().getBookingState() == BOOKINGSTATE.BOOKED) {
                    passenger = clientRepository.findPassengerById(ticket.get().getUid());
                    totalAdult = ticket.get().getTotalAdult();
                    totalChildren = ticket.get().getTotalChildren();
                    totalBaby = ticket.get().getTotalBaby();
                }

                // Gán thông tin trả về
                TicketDTO ticketDTO = mapToTicketDTO(ticket.get(), flightSchedule.get(), flightEntity.get(), locationTo,
                        locationFrom, airplane.get(), passenger, totalAdult, totalChildren, totalBaby);
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
        dto.setRowSeat(ticketCommon.getRowSeat());
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

    public ResponseCommon cancelTicket(Integer ticketId) throws UnsupportedEncodingException, MessagingException {

        // Hàm tìm thông tin vé theo Id của vé
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        // Kiểm tra vé tìm được có phải rỗng hay không? nếu rỗng trả về lỗi.
        if (ticket.isEmpty()) throw new ErrorException("Không Tìm Thấy Vé");
        // Kiểm tra vé đó đã được đặt hay chưa hay chưa? nếu đã được đặt thì trả về lỗi
        if (ticket.get().getBookingState().equals(BOOKINGSTATE.AVAILABLE)) throw new ErrorException("Vé Chưa Được Đặt Nên Không Thể Hủy");
        // Thay đổi trạng thái vé từ BOOKED sang CANCELED
        ticket.get().setBookingState(BOOKINGSTATE.AVAILABLE);
        ticket.get().setTotalAdult(0);
        ticket.get().setTotalChildren(0);
        ticket.get().setTotalBaby(0);
        ticket.get().setTotalPrice(0L);
        // Cập nhật thông tin vé vào database
        ticketRepository.saveAndFlush(ticket.get());

        // Lấy thông tin của khách hàng đã đặt vé
        Passenger passenger = clientRepository.getById(ticket.get().getUid());
        // Lấy FlightSchedule của vé
        FlightSchedule flightSchedule = flightScheduleRepository.findFlightSchedulesByFlightScheduleId(ticket.get().getFlightScheduleId()).get();
        // Gửi email tới người dùng
        sendTicketCancelEmail(passenger, ticket.get(), flightSchedule);

        // Trả về các thông tin cho phía client.
        ResponseCommon response = new ResponseCommon();
        response.setCode(200);
        response.setResult("Hủy Vé Thành Công");

        return response;
    }

    private TicketDTO mapToTicketDTO(Ticket ticket, FlightSchedule flightSchedule, FlightEntity flightEntity,
                                     Location locationTo, Location locationFrom, Airplane airplane, Passenger passenger,
                                     Integer totalAdult, Integer totalChildren, Integer totalBaby) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setRowSeat(ticket.getRowSeat());
        ticketDTO.setTicketType(ticket.getRowSeat() + "_" + TICKETTYPE.getValue(ticket.getTicketType().name()).value);
        ticketDTO.setPrice(mapPriceDTO(totalAdult, totalChildren, totalBaby, ticket.getPrice()));
        ticketDTO.setBookingState(ticket.getBookingState().name());
        ticketDTO.setAirplaneDTO(
                new AirplaneDTO(null, airplane.getAirplaneName(), null, airplane.getBrand(), airplane.getLinkImgBrand()));
        ticketDTO.setFlightSchedule(convertFlightScheduleToDTO(flightSchedule));
        ticketDTO.setFlightDTO(convertFlightEntityToDTO(flightEntity, locationTo, locationFrom));
        ticketDTO.setUserDetailDTO(Objects.nonNull(passenger) ? mapPassengerToUserDetailDTO(passenger) : null);

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

    private UserDetailDTO mapPassengerToUserDetailDTO(Passenger passenger) {
        return UserDetailDTO.builder()
                .id(passenger.getId())
                .email(passenger.getEmail())
                .username(passenger.getFullName())
                .phone(passenger.getPhoneNo())
                .build();
    }

    private TicketDTO.PriceDTO mapPriceDTO(Integer totalAdult, Integer totalChildren, Integer totalBaby, Long price) {
        long childrenPrice = price * 2/ 3;
        long babyPrice = price / 2;
        long totalPrice = price * totalAdult + totalChildren * childrenPrice + totalBaby * babyPrice;
        long tax = totalPrice / 10;

        TicketDTO.PriceDTO priceDTO = new TicketDTO.PriceDTO();
        priceDTO.setAdultPrice(new DetailPriceDTO(totalAdult, price, totalAdult * price));
        priceDTO.setChildrenPrice(totalChildren == 0 ? null : new DetailPriceDTO(totalChildren, childrenPrice, totalChildren * childrenPrice));
        priceDTO.setBabyPrice(totalBaby == 0 ? null : new DetailPriceDTO(totalBaby, babyPrice, totalBaby * babyPrice));
        priceDTO.setTax(tax);
        priceDTO.setTotalPrice(totalPrice);

        return priceDTO;
    }

    // Hàm thực hiện gửi thông tin huỷ vé về mail
    private void sendTicketCancelEmail(Passenger passenger, Ticket ticket, FlightSchedule schedule)
            throws UnsupportedEncodingException, MessagingException {

        String subject = Constants.FLIGHT_CANCELED_SUBJECT;
        String content = Constants.FLIGHT_CANCELED_CONTENT;

        content = content.replace("[[name]]", passenger.getFullName());
        content = content.replace("[[flightNo]]", schedule.getFlightNo());
        content = content.replace("[[rowSeat]]", ticket.getRowSeat());

        String[] values = new String[]{passenger.getEmail()};
        Helper.sendMailCommon(values, subject, content);
    }

    // Hàm tạo vé cho flight schedule
    public ResponseCommon createTicketsForFlightSchedule(TicketCreateDTO ticketCreateDTO) {
        if (Objects.nonNull(ticketCreateDTO)) {
            Objects.requireNonNull(ticketCreateDTO.getFirstClassNumber());
            Objects.requireNonNull(ticketCreateDTO.getBusinessClassNumber());
            Objects.requireNonNull(ticketCreateDTO.getPremiumClassNumber());
            Objects.requireNonNull(ticketCreateDTO.getEconomyClassNumber());
            Objects.requireNonNull(ticketCreateDTO.getFlightScheduleId());

            FlightSchedule flightSchedule = flightScheduleRepository.findFlightSchedulesByFlightScheduleId(ticketCreateDTO.getFlightScheduleId()).orElse(null);
            if (flightSchedule == null) {
                throw new ErrorException(Constants.FLIGHT_SCHEDULE_NON_EXISTENT);
            }

            Integer flightScheduleId = flightSchedule.getFlightScheduleId();
            List<Ticket> tickets = new ArrayList<>();
            // tạo vé cho loại vé FIRST_CLASS
            int firstClassNumber =  ticketCreateDTO.getFirstClassNumber();
            if (firstClassNumber > 0) {
                addTicket(flightScheduleId, tickets, firstClassNumber, TICKETTYPE.FIRST_CLASS, 7000L, 70);
            }

            // tạo vé cho loại vé BUSINESS_CLASS
            int businessClassNumber = ticketCreateDTO.getBusinessClassNumber();
            if (businessClassNumber > 0) {
                addTicket(flightScheduleId, tickets, businessClassNumber, TICKETTYPE.BUSINESS_CLASS, 6000L, 60);
            }

            // tạo vé cho loại vé PREMIUM_CLASS
            int premiumClassNumber = ticketCreateDTO.getPremiumClassNumber();
            if (premiumClassNumber > 0) {
                addTicket(flightScheduleId, tickets, premiumClassNumber, TICKETTYPE.PREMIUM_CLASS, 5000L, 50);
            }

            // tạo vé cho loại vé ECONOMY_CLASS
            int economyClassNumber = ticketCreateDTO.getEconomyClassNumber();
            if (economyClassNumber > 0) {
                addTicket(flightScheduleId, tickets, economyClassNumber, TICKETTYPE.ECONOMY_CLASS, 4000L, 40);
            }

            // Tạo tên seatRow cho từng vé theo fomart: [A-Z]d{2} => VD: A12
            Random random = new Random();
            String valueRow = null;
            int number;
            for (int i = 0; i < tickets.size(); i++) {
                number = random.nextInt(90) + 10;
                if (i % 6 == 0) {
                    valueRow = "A" + number;
                }
                if (i % 6 == 1) {
                    valueRow = "B" + number;
                }
                if (i % 6 == 2) {
                    valueRow = "C" + number;
                }
                if (i % 6 == 3) {
                    valueRow = "D" + number;
                }
                if (i % 6 == 4) {
                    valueRow = "E" + number;
                }
                if (i % 6 == 5) {
                    valueRow = "F" + number;
                }

                tickets.get(i).setRowSeat(valueRow);
            }

            // Lưu tất cả vé
            ticketRepository.saveAll(tickets);
        } else throw new ErrorException("Invalid Request");

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(Constants.TICKET_CREATED_SUCCESS);
        return responseCommon;
    }

    // Hàm tạo từng chiếc vè và cho vào danh sách List<Ticket> tickets
    private void addTicket(Integer flightScheduleId, List<Ticket> tickets, int classNumber, TICKETTYPE tickettype, long price, int weightPackage) {
        Ticket ticket = null;
        for (int i = 0; i < classNumber; i++) {
            ticket = new Ticket();
            ticket.setTicketType(tickettype);
            ticket.setFlightScheduleId(flightScheduleId);
            ticket.setWeightPackage(weightPackage);
            ticket.setPrice(price);
            ticket.setBookingState(BOOKINGSTATE.AVAILABLE);
            ticket.setTotalAdult(0);
            ticket.setTotalChildren(0);
            ticket.setTotalBaby(0);
            ticket.setTotalPrice(0L);
            tickets.add(ticket);
        }
    }
}
