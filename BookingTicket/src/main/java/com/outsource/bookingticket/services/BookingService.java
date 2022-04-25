package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.BookingRequestDto;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import com.outsource.bookingticket.entities.ticket.Ticket;
import com.outsource.bookingticket.entities.users.Passenger;
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
    public ResponseCommon bookingFlight(BookingRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {

        // Kiểm tra requestDto có null không? nếu null trả ra lỗi
        if (Objects.nonNull(requestDto)) {
            // Hàm tìm kiếm lịch trình 1 chuyến bay theo ID của lịch trình chuyến bay cần tìm
            FlightSchedule flightScheduleTo = saveSchedule(requestDto.getFlightScheduleIdTo());
            Passenger passenger = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());

            if (checkAvailableSeat(flightScheduleTo.getAvailableSeat()))throw new ErrorException(Constants.SEAT_UNAVAILABLE);

            if (requestDto.getFlightScheduleIdBack() !=  null){
                FlightSchedule flightScheduleBack = saveSchedule(requestDto.getFlightScheduleIdBack());
                if (checkAvailableSeat(flightScheduleBack.getAvailableSeat()))throw new ErrorException(Constants.SEAT_UNAVAILABLE);

                Ticket ticketTo = new Ticket();
                ticketTo.setFlightScheduleId(flightScheduleTo.getFlightScheduleId());
                ticketTo.setPrice(requestDto.getPriceTo());
                ticketTo.setBookingState(BOOKINGSTATE.BOOKED);
                Passenger passengerTo = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());
                ticketTo.setUid(passengerTo.getId());
                // Lưu dối tượng Ticket vào database.
                ticketRepository.save(ticketTo);

                // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
                flightScheduleTo.setAvailableSeat(flightScheduleTo.getAvailableSeat() - 1);
                // Cập nhật thông tin vào database
                flightScheduleRepository.saveAndFlush(flightScheduleTo);

                Ticket ticketBack = new Ticket();
                ticketBack.setFlightScheduleId(flightScheduleBack.getFlightScheduleId());
                ticketBack.setPrice(requestDto.getPriceBack());
                ticketBack.setBookingState(BOOKINGSTATE.BOOKED);
                Passenger passengerBack = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());
                ticketBack.setUid(passengerBack.getId());
                // Lưu dối tượng Ticket vào database.
                ticketRepository.save(ticketBack);

                // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
                flightScheduleBack.setAvailableSeat(flightScheduleBack.getAvailableSeat() - 1);
                // Cập nhật thông tin vào database
                flightScheduleRepository.saveAndFlush(flightScheduleBack);

                // Gọi hàm sendBookingSuccessEmail() để gửi thông tin vé về mail
                sendBookingSuccessEmailKhuHoi(passenger, ticketTo, ticketBack, flightScheduleTo, flightScheduleBack);

                ResponseCommon responseCommon = new ResponseCommon();
                responseCommon.setCode(200);
                responseCommon.setResult(Constants.BOOKING_SUCCESS);
                return responseCommon;

            }

            // Khởi tạo 1 đối tượng của Ticket để set dữ liệu cho đối tượng đó. Dữ liệu được lấy từ tham số truyền vào
            Ticket ticket = new Ticket();
            ticket.setFlightScheduleId(flightScheduleTo.getFlightScheduleId());
            ticket.setPrice(requestDto.getPriceTo());
            ticket.setBookingState(BOOKINGSTATE.BOOKED);
            ticket.setUid(passenger.getId());
            // Lưu dối tượng Ticket vào database.
            ticketRepository.save(ticket);

            // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
            flightScheduleTo.setAvailableSeat(flightScheduleTo.getAvailableSeat() - 1);
            // Cập nhật thông tin vào database
            flightScheduleRepository.saveAndFlush(flightScheduleTo);

            // Gọi hàm sendBookingSuccessEmail() để gửi thông tin vé về mail
            sendBookingSuccessEmail(passenger, ticket, flightScheduleTo);

        } else throw new ErrorException("Invalid Request");

        // Trả về các thông tin cho phía client.
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(Constants.BOOKING_SUCCESS);
        return responseCommon;
    }

    // Hàm thực hiện gửi thông tin vé về mail
    private void sendBookingSuccessEmail(Passenger passenger, Ticket ticket, FlightSchedule schedule)
            throws UnsupportedEncodingException, MessagingException {

        String subject = Constants.BOOKING_SUCCESS_SUBJECT;
        String content = Constants.BOOKING_SUCCESS_CONTENT;

        content = content.replace("[[name]]", passenger.getFullName());
        content = content.replace("[[flightNo]]", schedule.getFlightNo());
        content = content.replace("[[start]]", convertLocalDatetimeToString(schedule.getStartTime()));
        content = content.replace("[[end]]", convertLocalDatetimeToString(schedule.getEndTime()));
        content = content.replace("[[totalPrice]]", ticket.getPrice().toString());

        String[] values = new String[]{passenger.getEmail()};
        Helper.sendMailCommon(values, subject, content);

    }

    // Hàm thực hiện gửi thông tin vé về mail khứ hồi
    private void sendBookingSuccessEmailKhuHoi(Passenger passenger, Ticket ticketTo, Ticket ticketBack, FlightSchedule scheduleTo, FlightSchedule scheduleBack)
            throws UnsupportedEncodingException, MessagingException {

        String subject = Constants.BOOKING_SUCCESS_SUBJECT;
        String content = Constants.BOOKING_SUCCESS_CONTENT_KHU_HOI;

        content = content.replace("[[name]]", passenger.getFullName());
        content = content.replace("[[flightNo]]", scheduleTo.getFlightNo());
        content = content.replace("[[start]]", convertLocalDatetimeToString(scheduleTo.getStartTime()));
        content = content.replace("[[end]]", convertLocalDatetimeToString(scheduleTo.getEndTime()));
        content = content.replace("[[totalPrice]]", ticketTo.getPrice().toString());

        content = content.replace("[[flightNoVe]]", scheduleBack.getFlightNo());
        content = content.replace("[[startVe]]", convertLocalDatetimeToString(scheduleBack.getStartTime()));
        content = content.replace("[[endVe]]", convertLocalDatetimeToString(scheduleBack.getEndTime()));
        content = content.replace("[[totalPriceVe]]", ticketBack.getPrice().toString());

        String[] values = new String[]{passenger.getEmail()};
        Helper.sendMailCommon(values, subject, content);

    }

    private Passenger getClient(String fullName, String phoneNo, String email){
        Optional<Passenger> passenger = clientRepository.findClientByPhoneNo(phoneNo);
        if (passenger.isEmpty()) {
            Passenger newPassenger = new Passenger();
            newPassenger.setFullName(fullName);
            newPassenger.setPhoneNo(phoneNo);
            newPassenger.setEmail(email);
            newPassenger = clientRepository.save(newPassenger);
            return newPassenger;
        }
        return passenger.get();
    }

    private FlightSchedule saveSchedule(Integer flightScheduleId){
        if (Objects.nonNull(flightScheduleId)) {
            // Hàm tìm kiếm lịch trình 1 chuyến bay theo ID của lịch trình chuyến bay cần tìm
            Optional<FlightSchedule> flightSchedule = flightScheduleRepository.findById(flightScheduleId);
            // Kiểm tra flightSchedule có rỗng hay không? nếu có trả ra lỗi
            if (flightSchedule.isEmpty()) throw new ErrorException("Chuyến Bay Không Tồn Tại");

            return flightSchedule.get();
        }
        return null;
    }

    private Boolean checkAvailableSeat(Integer totalSeat){
        if (totalSeat <= 0) return true;
        else return false;
    }
}
