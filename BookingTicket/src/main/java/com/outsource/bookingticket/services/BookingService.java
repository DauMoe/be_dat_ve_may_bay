package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.Helper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class BookingService extends BaseService {

    // Hàm đặt vé máy bay với tham số truyền vào là requestDto
    public ResponseCommon bookingFlight(String token, BookingRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {

        Integer userId = jwtTokenProvider.getUserIdFromJWT(getTokenFromHeader(token));

        // Kiểm tra requestDto có null không? nếu null trả ra lỗi
        if (Objects.nonNull(requestDto)) {
            UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ErrorException("Invalid User"));
            // Hàm tìm kiếm lịch trình 1 chuyến bay theo ID của lịch trình chuyến bay cần tìm
            Optional<FlightSchedule> flightSchedule = flightScheduleRepository.findById(requestDto.getFlightScheduleId());
            // Kiểm tra flightSchedule có rỗng hay không? nếu có trả ra lỗi
            if (flightSchedule.isEmpty()) throw new ErrorException("Chuyến Bay Không Tồn Tại");
            // Hàm tìm kiếm vé máy bay theo số ghế và ID của chuyến bay
            Ticket ticketExist = ticketRepository
                    .findBySeatNumberAndFlightScheduleId(requestDto.getSeatNumber(), requestDto.getFlightScheduleId());
            // Hàm kiểm tra vé vừa tìm được bên trên. Nếu vé đó tồn tài thì trả về lỗi.
            if (Objects.nonNull(ticketExist)
                    && ticketExist.getBookingState().equals(BOOKINGSTATE.BOOKED))
                throw new ErrorException("Số Ghế Đã Được Đặt");

            //Kiểm tra vé còn hay đã hết.
            Integer seatAvailable = flightSchedule.get().getAvailableSeat();
            if (seatAvailable <= 0) throw new ErrorException(Constants.SEAT_UNAVAILABLE);

            // Khởi tạo 1 đối tượng của Ticket để set dữ liệu cho đối tượng đó. Dữ liệu được lấy từ tham số truyền vào
            Ticket ticket = new Ticket();
            ticket.setFlightScheduleId(flightSchedule.get().getFlightScheduleId());
            ticket.setSeatNumber(requestDto.getSeatNumber());
            ticket.setPrice(requestDto.getPrice());
            ticket.setBookingState(BOOKINGSTATE.BOOKED);
            ticket.setUid(userId);
            // Lưu dối tượng Ticket vào database.
            ticketRepository.save(ticket);

            // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
            flightSchedule.get().setAvailableSeat(flightSchedule.get().getAvailableSeat() - 1);
            // Cập nhật thông tin vào database
            flightScheduleRepository.saveAndFlush(flightSchedule.get());

            // Gọi hàm sendBookingSuccessEmail() để gửi thông tin vé về mail
            sendBookingSuccessEmail(user, ticket, flightSchedule.get());


        } else throw new ErrorException("Invalid Request");

        // Trả về các thông tin cho phía client.
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(Constants.BOOKING_SUCCESS);
        return responseCommon;
    }

    // Hàm thực hiện gửi thông tin vé về mail
    private void sendBookingSuccessEmail(UserEntity user, Ticket ticket, FlightSchedule schedule)
            throws UnsupportedEncodingException, MessagingException {

        String subject = Constants.BOOKING_SUCCESS_SUBJECT;
        String content = Constants.BOOKING_SUCCESS_CONTENT;

        content = content.replace("[[name]]", user.getUsername());
        content = content.replace("[[flightNo]]", schedule.getFlightNo());
        content = content.replace("[[start]]", convertLocalDatetimeToString(schedule.getStartTime()));
        content = content.replace("[[end]]", convertLocalDatetimeToString(schedule.getEndTime()));
        content = content.replace("[[seatNumber]]", ticket.getSeatNumber());

        String[] values = new String[]{user.getEmail()};
        Helper.sendMailCommon(values, subject, content);

    }
}
