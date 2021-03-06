package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.airport.AirportGeo;
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
import org.springframework.util.CollectionUtils;

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
import java.util.stream.Collectors;

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

    @Autowired protected AirportGeoRepository airportGeoRepository;

    // H??m format date t??? String sang LocalDatetime
    protected LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try {
            // Format d???ng ng??y/th??ng/n??m ????? tr??? v???
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateTimeString);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            // Tr??? v??? l???i n???u tham s??? truy???n v??o kh??ng ????ng d???ng
            throw new ErrorException(MessageUtil.DATETIME_ERROR);
        }
    }

    protected String convertLocalDatetimeToString(LocalDateTime dateTime) {
        // H??m format ?????nh d???ng ng??y/th??ng/n??m gi???:ph??t:gi??y
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    protected String convertLocalDatetimeToHourString(LocalDateTime dateTime) {
        // H??m format ?????nh d???ng ng??y/th??ng/n??m gi???:ph??t:gi??y
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    // H??m l???y th??ng tin chuy???n bay
    protected Ticket getTicket(Integer ticketId) {
        // Ki???m tra ID v?? bay kh??ng r???ng s??? t??m ki???m; n???u r???ng s??? tr??? ra th??ng b??o l???i
        if (Objects.nonNull(ticketId)) {
            // T??m th??ng tin chuy???n bay theo flightId
            Optional<Ticket> ticketOptional = ticketRepository.findTicketByTicketId(ticketId);
            // Ki???m tra d??? li???u c?? t???n t???i
            if (ticketOptional.isPresent()) {
                // Tr??? v??? th??ng tin v?? bay
                return ticketOptional.get();
            }
        }
        throw new ErrorException(MessageUtil.TICKET_NOT_FOUND_EX);
    }

    protected FlightSchedule getFlightSchedule(Integer flightScheduleId) {
        // Ki???m tra ID l???ch tr??nh bay kh??ng r???ng s??? t??m ki???m; n???u r???ng s??? tr??? ra th??ng b??o l???i
        if (Objects.nonNull(flightScheduleId)) {
            // T??m th??ng tin chuy???n bay theo flightId
            Optional<FlightSchedule> flightScheduleOptional =
                    flightScheduleRepository.findFlightSchedulesByFlightScheduleId(flightScheduleId);
            // Ki???m tra d??? li???u c?? t???n t???i
            if (flightScheduleOptional.isPresent()) {
                // Tr??? v??? th??ng tin l???ch tr??nh bay
                return flightScheduleOptional.get();
            }
        }
        throw new ErrorException(MessageUtil.FLIGHT_SCHEDULE_NOT_FOUND_EX);
    }

    // H??m c???t chu???i token ????? lo???i b??? 7 k?? t??? ?????u ti???n c???a token (Bearer ).
    protected String getTokenFromHeader(String tokenHeader) {
        return tokenHeader.substring(7);
    }

    // L???y ?????a ??i???m bay theo FlightEntity
    protected List<Location> getAllLocationByFlight(List<FlightEntity> listFlight) {
        // L???y ID c???a t???t c??? ?????a ??i???m trong danh s??ch chuy???n bay
        Set<Integer> listLocationId = new HashSet<>();
        listFlight.forEach(i -> listLocationId.add(i.getFromAirportId()));
        listFlight.forEach(i -> listLocationId.add(i.getToAirportId()));

        // L???y h???t ?????a ??i???m bay
        return locationRepository.findLocationsByLocationIdIn(listLocationId);
    }

    // H??m ki???m tra ?????a ??i???m ???? ???????c s??? d???ng ??? chuy???n bay n??o ch??a
    protected boolean checkLocationUsedInFlight(Integer locationId) {
        // T??m h???t c??c AirportGeo theo Id ?????a ??i???m
        List<AirportGeo> airportGeoList = airportGeoRepository.findAirportGeosByLocationId(locationId);
        // L???c c??c Id airportGeo theo danh s??ch v???a t??m ???????c
        List<Integer> airportGeoIdList = airportGeoList
                .stream()
                .map(AirportGeo::getLocationId).distinct()
                .collect(Collectors.toList());
        // T??m ki???m danh s??ch bay c?? ?????a ??i???m ??i v?? ?????n trong danh s??ch airportGeo
        List<FlightEntity> flightEntityList =
                flightRepository.findByFromAirportIdInOrToAirportIdIn(airportGeoIdList, airportGeoIdList);
        // Ki???m tra danh s??ch n??y c?? r???ng hay kh??ng
        return CollectionUtils.isEmpty(flightEntityList);
    }
}
