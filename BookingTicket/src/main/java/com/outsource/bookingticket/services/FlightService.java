package com.outsource.bookingticket.services;

import com.outsource.bookingticket.dtos.FlightCommonDTO;
import com.outsource.bookingticket.dtos.FlightResponseDTO;
import com.outsource.bookingticket.entities.common.FlightCommon;
import com.outsource.bookingticket.entities.common.FlightTicketEntity;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.enums.FLIGHTSTATE;
import com.outsource.bookingticket.entities.flight.FlightEntity;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.location.Location;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService extends BaseService {

    // Hàm tìm kiếm chuyến bay theo vị trí khởi hành, kết thúc hoặc mã chuyến bay
    public ResponseEntity<?> searchFlight(Integer fromAirportId, Integer toAirportId, String startTime, String endTime,
                                          Integer totalAdult, Integer totalChildren, Integer totalBaby) {
        // Kiểm tra nếu các biến truyền vào đều null thì sẽ trả ra thông báo lỗi
        if (Objects.isNull(fromAirportId) && Objects.isNull(toAirportId) && Objects.isNull(startTime)) {
            throw new ErrorException(MessageUtil.FILL_TO_SEARCH);
        }
        // Lọc ra những lịch trình có số ghế còn trống bằng 0
        List<FlightSchedule> flightScheduleList = flightScheduleRepository.findByAvailableSeat(0);

        // Kiểm tra nếu danh sách khác rỗng sẽ huỷ hết các vé chưa được đặt
        if (!CollectionUtils.isEmpty(flightScheduleList)) {
            List<Integer> flightScheduleIdToCancel = flightScheduleList.stream()
                                                    .map(FlightSchedule::getFlightScheduleId)
                                                    .collect(Collectors.toList());
            // Hàm huỷ các vé của lịch trình mà đã được đặt đủ hết số ghế
            if (!CollectionUtils.isEmpty(flightScheduleIdToCancel)) {
                List<Ticket> ticketList = ticketRepository.findByFlightScheduleIdIn(flightScheduleIdToCancel);
                List<Ticket> ticketCancelList = ticketList.stream()
                        .filter(p -> p.getBookingState() != BOOKINGSTATE.AVAILABLE && p.getBookingState() != BOOKINGSTATE.PENDING)
                        .collect(Collectors.toList());
                ticketCancelList.forEach(p -> p.setBookingState(BOOKINGSTATE.CANCELED));
                ticketRepository.saveAll(ticketCancelList);
            }
        }

        // Cộng tổng số người cần đặt vé
        Integer totalPeople = totalAdult + totalChildren + totalBaby;

        // Gọi tới hàm tìm kiếm theo vị trí khởi hành, kết thúc hoặc mã chuyến bay
        List<FlightTicketEntity> flightToList = searchListFlight(fromAirportId, toAirportId, startTime, totalPeople);
        List<FlightResponseDTO> flightToResponseReturnList = new ArrayList<>();

        if (Objects.nonNull(endTime)) {
            List<FlightTicketEntity> flightReturnList = searchListFlight(toAirportId, fromAirportId, endTime, totalPeople);
            flightToResponseReturnList = convertFlightEntityToListDTO(flightReturnList);
        }
        // Convert danh sách FlightEntity sang danh sách FlightScheduleResponseDTO
        List<FlightResponseDTO> flightResponseToList = convertFlightEntityToListDTO(flightToList);

        // Nếu không tìm vé khứ hồi sẽ trả về danh sách vé 1 chiều
        if (!StringUtils.hasLength(endTime)) {
            // Danh sách trả về vé 1 chiều rỗng sẽ trả về lỗi, ngược lại sẽ trả về kết quả
            if (CollectionUtils.isEmpty(flightResponseToList)) {
                throw new ErrorException(MessageUtil.NOT_HAVE_ANY_FLIGHT);
            } else {
                return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(flightResponseToList)));
            }
        } else {
            // Người dùng có tìm cả vé khứ hồi
            // Nếu danh sách bay cả đi và về đều không có sẽ trả ra lỗi
            if (CollectionUtils.isEmpty(flightResponseToList) && CollectionUtils.isEmpty(flightToResponseReturnList)) {
                throw new ErrorException(MessageUtil.NOT_HAVE_ANY_FLIGHT);
            } else {
                // Trả về danh sách bay đi và danh sách bay về
                return ResponseEntity.ok(Helper.createSuccessToFromListCommon(
                        new ArrayList<>(flightResponseToList), new ArrayList<>(flightToResponseReturnList)));
            }
        }
    }

    // Lấy hết thông tin chuyến bay (cho quản lý)
    public ResponseEntity<?> getAllFlight(Integer fromAirportId, Integer toAirportId, String flightNo) {
        // Gọi hàm lấy hết thông tin chuyến bay
        List<FlightCommon> flightCommonList;
        // Xử lý lấy ra danh sách chuyến bay
        // Nếu không có điều kiện sẽ lấy hết danh sách
        if (Objects.isNull(fromAirportId) && Objects.isNull(toAirportId) && Objects.isNull(flightNo)) {
            flightCommonList = flightCommonRepository.findAllFlight();
            // Lấy danh sách chuyến bay theo địa điểm đến và đi
        } else if (Objects.nonNull(fromAirportId) && Objects.nonNull(toAirportId) && Objects.isNull(flightNo)) {
            flightCommonList = flightCommonRepository.findAllFlightByLocation(fromAirportId, toAirportId);
            // Lấy danh sách chuyến bay theo địa điểm bắt đầu
        } else if (Objects.nonNull(fromAirportId) && Objects.isNull(toAirportId) && Objects.isNull(flightNo)) {
            flightCommonList = flightCommonRepository.findAllFlightByFromLocation(fromAirportId);
            // Lấy danh sách chuyến bay theo địa điểm đến
        } else if (Objects.isNull(fromAirportId) && Objects.nonNull(toAirportId) && Objects.isNull(flightNo)) {
            flightCommonList = flightCommonRepository.findAllFlightByToLocation(toAirportId);
            // Lấy danh sách chuyến bay theo mã chuyến bay
        } else if (Objects.isNull(fromAirportId) && Objects.isNull(toAirportId)) {
            flightCommonList = flightCommonRepository.findAllFlightByFlightNo(flightNo);
            // Lấy danh sách chuyến bay theo địa điểm đến, đi và mã chuyến bay
        } else if (Objects.nonNull(fromAirportId) && Objects.nonNull(toAirportId)) {
            flightCommonList = flightCommonRepository.findAllFlightByLocationAndFlightNo(fromAirportId, toAirportId, flightNo);
            // Trả về lỗi nếu tìm kiếm sai
        } else throw new ErrorException(MessageUtil.FILL_SEARCH_AGAIN);

        // Danh sách mã địa điểm đến và đi
        Set<Integer> locationIdList = new HashSet<>();
        flightCommonList.forEach(i -> {
            locationIdList.add(i.getToAirportId());
            locationIdList.add(i.getFromAirportId());
        });
        // Lấy hết các thông tin về địa điểm theo danh sách mã địa điểm
        List<Location> locationList = locationRepository.findLocationsByLocationIdIn(locationIdList);

        // Gán dữ liệu để trả về
        List<FlightCommonDTO> flightCommonDTOList = convertFlightCommonToListDTO(flightCommonList, locationList);
        // Trả về dữ liệu thành công
        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(flightCommonDTOList)));
    }

    // Hàm khoá chuyến bay
    public ResponseEntity<?> updateFlightState(Integer flightId) {
        // Tìm kiếm chuyến bay theo mã chuyến bay
        Optional<FlightEntity> flightEntityOp = flightRepository.findFlightEntityByFlightId(flightId);
        // Kiểm tra nếu rỗng trả ra lỗi
        if (flightEntityOp.isEmpty()) {
            throw new ErrorException(MessageUtil.FLIGHT_NOT_FOUND_EX);
        }
        // Lấy dữ liệu chuyến bay
        FlightEntity flightEntity = flightEntityOp.get();
        // Lấy danh sách lịch trình bay theo mã chuyến bay
        List<FlightSchedule> flightScheduleList = flightScheduleRepository.findFlightSchedulesByFlightNo(flightEntity.getFlightNo());
        // Lấy hết vé theo mã chuyến bay
        List<Ticket> tickets = ticketRepository.findTicketByFlightNoAndState(flightEntity.getFlightNo());
        // Kiểm tra danh sách vé không rỗng thì sẽ huỷ hết các vé này
        if (!CollectionUtils.isEmpty(tickets)) {
            tickets.forEach(t -> t.setBookingState(BOOKINGSTATE.CANCELED));
            ticketRepository.saveAll(tickets);
        }
        // Thay đổi trạng thái chuyến bay sang huỷ
        flightScheduleList.forEach(i -> i.setFlightState(FLIGHTSTATE.FLIGHT_OFF));
        // Lưu vào hệ thống dữ liệu
        flightScheduleRepository.saveAll(flightScheduleList);
        // Trả về khoá chuyến bay thành công
        return ResponseEntity.ok(Helper.createSuccessCommon(MessageUtil.LOCK_SUCCESS));
    }

    // Hàm lấy danh sách gợi ý bay
    public ResponseEntity<?> getSuggestionFlight() {
        // Lấy gợi ý danh sách bay, lấy 6 chuyến bay có giá rẻ nhất
        List<FlightTicketEntity> flightSuggestionList = flightTicketRepository.findAllFlightSuggestion();

        // Convert danh sách FlightEntity sang danh sách FlightScheduleResponseDTO
        List<FlightResponseDTO> flightSuggestionResponseList = convertFlightEntityToListDTO(flightSuggestionList);

        // Trả về kết quả
        return ResponseEntity.ok(Helper.createSuccessListCommon(new ArrayList<>(flightSuggestionResponseList)));
    }

    // Hàm tìm kiếm chuyến bay
    private List<FlightTicketEntity> searchListFlight(Integer fromAirportId, Integer toAirportId, String time, Integer totalPeople) {
        return flightTicketRepository.findAllFlightByLocationAndTime(time, fromAirportId, toAirportId, totalPeople);
    }

    // Hàm convert FlightEntity sang FlightResponseDTO
    private FlightResponseDTO convertFlightEntityToDTO(FlightTicketEntity flightEntity) {
        FlightResponseDTO responseDTO = new FlightResponseDTO();
        responseDTO.setFlightNo(flightEntity.getFlightNo());
        responseDTO.setFlightId(flightEntity.getFlightId());
        responseDTO.setWeightPackage(flightEntity.getWeightPackage());
        responseDTO.setStartTime(convertLocalDatetimeToHourString(flightEntity.getStartTime()));
        responseDTO.setEndTime(convertLocalDatetimeToHourString(flightEntity.getEndTime()));
        responseDTO.setPrice(withLargeIntegers(flightEntity.getPrice()));
        responseDTO.setFlightScheduleId(flightEntity.getFlightScheduleId());
        responseDTO.setTicketId(flightEntity.getTicketId());
        responseDTO.setBrand(flightEntity.getBrand());
        responseDTO.setLinkImageBrand(flightEntity.getLinkImgBrand());
        return responseDTO;
    }

    // Hàm lọc địa điểm trong danh sách địa điểm theo ID của địa điểm
    private Location getLocationById(Integer locationId, List<Location> locationList) {
        Optional<Location> location = locationList.stream().filter(i -> i.getLocationId().equals(locationId)).findFirst();
        return location.orElseGet(Location::new);
    }

    // Hàm convert danh sách FlightEntity sang danh sách FlightResponseDTO
    private List<FlightResponseDTO> convertFlightEntityToListDTO(List<FlightTicketEntity> flightEntityList) {
        return flightEntityList.stream().map(this::convertFlightEntityToDTO).collect(Collectors.toList());
    }

    private FlightCommonDTO convertToFlightCommonDTO(FlightCommon flightCommon, List<Location> locationList) {
        Location toLocation = getLocationById(flightCommon.getToAirportId(), locationList);
        Location fromLocation = getLocationById(flightCommon.getFromAirportId(), locationList);
        return FlightCommonDTO.builder()
                .flightId(flightCommon.getFlightId())
                .flightNo(flightCommon.getFlightNo())
                .fromAirport(mapLocation(fromLocation))
                .toAirport(mapLocation(toLocation))
                .startTime(convertLocalDatetimeToString(flightCommon.getStartTime()))
                .endTime(convertLocalDatetimeToString(flightCommon.getEndTime()))
                .availableSeat(flightCommon.getAvailableSeat())
                .airplaneName(flightCommon.getAirplaneName())
                .build();
    }

    // Hàm convert danh sách FlightCommon sang danh sách FlightCommonDTO
    private List<FlightCommonDTO> convertFlightCommonToListDTO(List<FlightCommon> flightCommonList, List<Location> locationList) {
        return flightCommonList.stream().map(i -> convertToFlightCommonDTO(i, locationList)).collect(Collectors.toList());
    }
}
