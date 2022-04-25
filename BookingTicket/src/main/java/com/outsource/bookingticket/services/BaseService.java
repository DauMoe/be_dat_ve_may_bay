package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.jwt.JwtTokenProvider;
import com.outsource.bookingticket.repositories.*;
import com.outsource.bookingticket.utils.MailUtil;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BaseService {

    @Autowired protected UserRepository userRepository;

    @Autowired protected FlightLogRepository flightLogRepository;

    @Autowired protected FlightRepository flightRepository;

    @Autowired protected TicketRepository ticketRepository;

    @Autowired protected FlightScheduleRepository flightScheduleRepository;

    @Autowired protected EntityManager entityManager;

    @Autowired protected LocationRepository locationRepository;

    @Autowired protected AirplaneRepository airplaneRepository;

    @Autowired protected FlightCommonRepository flightCommonRepository;

    @Autowired protected TicketCommonRepository ticketCommonRepository;

    @Autowired protected FlightNewsRepository flightNewsRepository;

    @Autowired protected FlightTicketRepository flightTicketRepository;

    @Autowired protected JwtTokenProvider jwtTokenProvider;

    @Autowired protected ClientRepository clientRepository;

    // Hàm format date từ String sang LocalDatetime
    protected LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try {
            // Format dạng ngày/tháng/năm để trả về
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateTimeString);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            // Trả về lỗi nếu tham số truyền vào không đúng dạng
            throw new ErrorException(MessageUtil.DATETIME_ERROR);
        }
    }

    protected String convertLocalDatetimeToString(LocalDateTime dateTime) {
        // Hàm format định dạng ngày/tháng/năm giờ:phút:giây
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    // Hàm lấy thông tin chuyến bay
    protected Ticket getTicket(Integer ticketId) {
        // Kiểm tra ID vé bay không rỗng sẽ tìm kiếm; nếu rỗng sẽ trả ra thông báo lỗi
        if (Objects.nonNull(ticketId)) {
            // Tìm thông tin chuyến bay theo flightId
            Optional<Ticket> ticketOptional = ticketRepository.findTicketByTicketId(ticketId);
            // Kiểm tra dữ liệu có tồn tại
            if (ticketOptional.isPresent()) {
                // Trả về thông tin vé bay
                return ticketOptional.get();
            }
        }
        throw new ErrorException(MessageUtil.TICKET_NOT_FOUND_EX);
    }

    protected FlightSchedule getFlightSchedule(Integer flightScheduleId) {
        // Kiểm tra ID lịch trình bay không rỗng sẽ tìm kiếm; nếu rỗng sẽ trả ra thông báo lỗi
        if (Objects.nonNull(flightScheduleId)) {
            // Tìm thông tin chuyến bay theo flightId
            Optional<FlightSchedule> flightScheduleOptional =
                    flightScheduleRepository.findFlightSchedulesByFlightScheduleId(flightScheduleId);
            // Kiểm tra dữ liệu có tồn tại
            if (flightScheduleOptional.isPresent()) {
                // Trả về thông tin lịch trình bay
                return flightScheduleOptional.get();
            }
        }
        throw new ErrorException(MessageUtil.FLIGHT_SCHEDULE_NOT_FOUND_EX);
    }

    // Hàm cắt chuỗi token để loại bỏ 7 ký tự đầu tiền của token (Bearer ).
    protected String getTokenFromHeader(String tokenHeader) {
        return tokenHeader.substring(7);
    }

    // Lấy địa điểm bay theo FlightEntity
    protected List<Location> getAllLocationByFlight(List<FlightEntity> listFlight) {
        // Lấy ID của tất cả địa điểm trong danh sách chuyến bay
        Set<Integer> listLocationId = new HashSet<>();
        listFlight.forEach(i -> listLocationId.add(i.getFromAirportId()));
        listFlight.forEach(i -> listLocationId.add(i.getToAirportId()));

        // Lấy hết địa điểm bay
        return locationRepository.findLocationsByLocationIdIn(listLocationId);
    }

}
