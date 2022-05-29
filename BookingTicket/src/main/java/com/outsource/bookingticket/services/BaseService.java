package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.AirplaneDTO;
import com.outsource.bookingticket.dtos.LocationDTO;
import com.outsource.bookingticket.entities.airplane.Airplane;
import com.outsource.bookingticket.entities.airport.AirportGeo;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.jwt.JwtTokenProvider;
import com.outsource.bookingticket.repositories.*;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BaseService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected FlightLogRepository flightLogRepository;

    @Autowired
    protected FlightRepository flightRepository;

    @Autowired
    protected TicketRepository ticketRepository;

    @Autowired
    protected FlightScheduleRepository flightScheduleRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected LocationRepository locationRepository;

    @Autowired
    protected AirplaneRepository airplaneRepository;

    @Autowired
    protected FlightCommonRepository flightCommonRepository;

    @Autowired
    protected TicketCommonRepository ticketCommonRepository;

    @Autowired
    protected FlightNewsRepository flightNewsRepository;

    @Autowired
    protected FlightTicketRepository flightTicketRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected AirportGeoRepository airportGeoRepository;

    @Autowired
    protected ScheduleRepository scheduleRepository;

    @Autowired
    protected CountTicketRepository countTicketRepository;

    // Hàm format date từ String sang LocalDatetime
    protected LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try {
            // Format dạng ngày/tháng/năm để trả về
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateTimeString);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            // Trả về lỗi nếu tham số truyền vào không đúng dạng
            throw new ErrorException(MessageUtil.DATETIME_ERROR);
        }
    }

    protected String formatDateTime(String dateTime, boolean isEndTime) {
        try {
            // Format dạng ngày/tháng/năm để trả về
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateTimeFormat = formatter2.format(formatter.parse(dateTime));
            return isEndTime ? dateTimeFormat + " 23:59:59" : dateTimeFormat;
        } catch (ErrorException e) {
            // Trả về lỗi nếu tham số truyền vào không đúng dạng
            throw new ErrorException(MessageUtil.DATETIME_ERROR);
        }
    }

    protected String convertLocalDatetimeToString(LocalDateTime dateTime) {
        // Hàm format định dạng ngày/tháng/năm giờ:phút:giây
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    protected String convertLocalDatetimeToHourString(LocalDateTime dateTime) {
        // Hàm format định dạng ngày/tháng/năm giờ:phút:giây
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
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

    // Hàm kiểm tra địa điểm đã được sử dụng ở chuyến bay nào chưa
    protected boolean checkLocationUsedInFlight(Integer locationId) {
        // Tìm hết các AirportGeo theo Id địa điểm
        List<AirportGeo> airportGeoList = airportGeoRepository.findAirportGeosByLocationId(locationId);
        // Lọc các Id airportGeo theo danh sách vừa tìm được
        List<Integer> airportGeoIdList = airportGeoList
                .stream()
                .map(AirportGeo::getLocationId).distinct()
                .collect(Collectors.toList());
        // Tìm kiếm danh sách bay có địa điểm đi và đến trong danh sách airportGeo
        List<FlightEntity> flightEntityList =
                flightRepository.findByFromAirportIdInOrToAirportIdIn(airportGeoIdList, airportGeoIdList);
        // Kiểm tra danh sách này có rỗng hay không
        return CollectionUtils.isEmpty(flightEntityList);
    }

    protected static String withLargeIntegers(Long value) {
        DecimalFormat df = new DecimalFormat("###,###,###,###");
        return df.format(value).replaceAll(",", ".");
    }

    // Hàm lọc Location theo ID
    protected Location filterLocation(List<Location> locationList, Integer locationId) {
        return locationList.stream().filter(l -> l.getLocationId().equals(locationId)).findFirst().orElse(null);
    }

    // Hàm chuyển Location sang LocationDTO để trả về
    protected LocationDTO mapLocation(Location location) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocationId(location.getLocationId());
        locationDTO.setCountry(Objects.nonNull(location.getCountryName()) ? location.getCountryName() : "");
        locationDTO.setCity(Objects.nonNull(location.getCityName()) ? location.getCityName() : "");
        return locationDTO;
    }

    // Hàm gán giá trị thuộc tính của Airplane sang AirplaneDTO
    protected AirplaneDTO mapToAirplaneDTO(Airplane airplane) {
        return AirplaneDTO.builder()
                .airplaneId(airplane.getAirplaneId())
                .airplaneName(airplane.getAirplaneName())
                .capacity(airplane.getCapacity())
                .brand(airplane.getBrand())
                .linkImgBrand(airplane.getLinkImgBrand())
                .build();
    }
}
