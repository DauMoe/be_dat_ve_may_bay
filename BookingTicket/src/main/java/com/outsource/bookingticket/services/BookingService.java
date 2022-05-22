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
import com.outsource.bookingticket.utils.MessageUtil;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

@Service
@Transactional
public class BookingService extends BaseService {

    // Hàm đặt vé máy bay với tham số truyền vào là requestDto
    public ResponseCommon bookingFlight(BookingRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {

        // Kiểm tra requestDto có null không? nếu null trả ra lỗi
        if (Objects.nonNull(requestDto)) {

            if (!validateEmail(requestDto.getEmail())) throw new ErrorException(Constants.EMAIL_NOT_VALID);

            Ticket ticketTo = ticketRepository.findTicketByTicketId(requestDto.getTicketIdTo()).get();
            if (ticketTo.getBookingState().equals(BOOKINGSTATE.BOOKED)) throw new ErrorException(MessageUtil.EXIST_TICKET);
            // Hàm tìm kiếm lịch trình 1 chuyến bay theo ID của lịch trình chuyến bay cần tìm
            FlightSchedule flightScheduleTo = saveSchedule(ticketTo.getFlightScheduleId());
            Passenger passenger = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());

            if (checkAvailableSeat(flightScheduleTo.getAvailableSeat()))throw new ErrorException(Constants.SEAT_UNAVAILABLE);

            if (requestDto.getTicketIdBack() !=  null){
                Ticket ticketBack = ticketRepository.findTicketByTicketId(requestDto.getTicketIdBack()).get();
                if (ticketBack.getBookingState().equals(BOOKINGSTATE.BOOKED)) throw new ErrorException(MessageUtil.EXIST_TICKET);
                FlightSchedule flightScheduleBack = saveSchedule(ticketBack.getFlightScheduleId());
                if (checkAvailableSeat(flightScheduleBack.getAvailableSeat()))throw new ErrorException(Constants.SEAT_UNAVAILABLE);

                ticketTo.setBookingState(BOOKINGSTATE.BOOKED);
                Passenger passengerTo = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());
                ticketTo.setUid(passengerTo.getId());
                ticketTo.setTotalAdult(requestDto.getTotalAdult());
                ticketTo.setTotalChildren(requestDto.getTotalChildren());
                ticketTo.setTotalBaby(requestDto.getTotalBaby());
                ticketTo.setTotalPrice(totalPrice(requestDto.getTotalAdult(), requestDto.getTotalChildren(),
                        requestDto.getTotalBaby(), ticketTo.getPrice()));

                Integer totalTicketTo = flightScheduleTo.getAvailableSeat() - requestDto.getTotalAdult() - requestDto.getTotalChildren() - requestDto.getTotalBaby();

                if (checkAvailableSeat(totalTicketTo)) throw new ErrorException(Constants.SEAT_UNAVAILABLE);

                flightScheduleTo.setAvailableSeat(totalTicketTo);

                // Cập nhật thông tin vào database
                flightScheduleRepository.saveAndFlush(flightScheduleTo);

                ticketBack.setBookingState(BOOKINGSTATE.BOOKED);
                Passenger passengerBack = getClient(requestDto.getNamePassenger(), requestDto.getPhoneNumber(), requestDto.getEmail());
                ticketBack.setUid(passengerBack.getId());
                ticketBack.setTotalAdult(requestDto.getTotalAdult());
                ticketBack.setTotalChildren(requestDto.getTotalChildren());
                ticketBack.setTotalBaby(requestDto.getTotalBaby());
                ticketBack.setTotalPrice(totalPrice(requestDto.getTotalAdult(), requestDto.getTotalChildren(),
                        requestDto.getTotalBaby(), ticketBack.getPrice()));

                // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
                Integer totalTicketBack = flightScheduleBack.getAvailableSeat() - requestDto.getTotalAdult() - requestDto.getTotalChildren() - requestDto.getTotalBaby();

                if (checkAvailableSeat(totalTicketBack)) throw new ErrorException(Constants.SEAT_UNAVAILABLE);

                flightScheduleBack.setAvailableSeat(totalTicketBack);

                if (Objects.nonNull(ticketBack) && Objects.nonNull(flightScheduleBack)) {

                    // Lưu dối tượng Ticket vào database.
                    ticketRepository.saveAndFlush(ticketTo);
                    // Lưu dối tượng Ticket vào database.
                    ticketBack =  ticketRepository.saveAndFlush(ticketBack);
                    // Cập nhật thông tin vào database
                    flightScheduleBack = flightScheduleRepository.saveAndFlush(flightScheduleBack);
                    // Gọi hàm sendBookingSuccessEmail() để gửi thông tin vé về mail
                    sendBookingSuccessEmailKhuHoi(passenger, ticketTo, ticketBack, flightScheduleTo, flightScheduleBack);

                    ResponseCommon responseCommon = new ResponseCommon();
                    responseCommon.setCode(200);
                    responseCommon.setResult(Constants.BOOKING_SUCCESS);
                    return responseCommon;

                }
                throw new ErrorException(MessageUtil.EXIST_TICKET) ;
            }

            // Khởi tạo 1 đối tượng của Ticket để set dữ liệu cho đối tượng đó. Dữ liệu được lấy từ tham số truyền vào
            ticketTo.setBookingState(BOOKINGSTATE.BOOKED);
            ticketTo.setUid(passenger.getId());
            ticketTo.setTotalAdult(requestDto.getTotalAdult());
            ticketTo.setTotalChildren(requestDto.getTotalChildren());
            ticketTo.setTotalBaby(requestDto.getTotalBaby());
            ticketTo.setTotalPrice(totalPrice(requestDto.getTotalAdult(), requestDto.getTotalChildren(),
                    requestDto.getTotalBaby(), ticketTo.getPrice()));

            Integer totalTicketTo = flightScheduleTo.getAvailableSeat() - requestDto.getTotalAdult() - requestDto.getTotalChildren() - requestDto.getTotalBaby();

            if (checkAvailableSeat(totalTicketTo)) throw new ErrorException(Constants.SEAT_UNAVAILABLE);

            // Lưu dối tượng Ticket vào database.
            ticketRepository.saveAndFlush(ticketTo);

            // Hàm cập nhật lại số lượng ghế còn trống trên chuyến bay đó.
            flightScheduleTo.setAvailableSeat(totalTicketTo);
            // Cập nhật thông tin vào database
            flightScheduleRepository.saveAndFlush(flightScheduleTo);

            // Gọi hàm sendBookingSuccessEmail() để gửi thông tin vé về mail
            sendBookingSuccessEmail(passenger, ticketTo, flightScheduleTo);

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
        content = content.replace("[[adult]]", ticket.getTotalAdult().toString());
        content = content.replace("[[children]]", ticket.getTotalChildren().toString());
        content = content.replace("[[baby]]", ticket.getTotalBaby().toString());
        content = content.replace("[[flightNo]]", schedule.getFlightNo());
        content = content.replace("[[start]]", convertLocalDatetimeToString(schedule.getStartTime()));
        content = content.replace("[[end]]", convertLocalDatetimeToString(schedule.getEndTime()));
        content = content.replace("[[totalPrice]]", ticket.getTotalPrice().toString());

        String[] values = new String[]{passenger.getEmail()};
        Helper.sendMailCommon(values, subject, content);

    }

    // Hàm thực hiện gửi thông tin vé về mail khứ hồi
    private void sendBookingSuccessEmailKhuHoi(Passenger passenger, Ticket ticketTo, Ticket ticketBack, FlightSchedule scheduleTo, FlightSchedule scheduleBack)
            throws UnsupportedEncodingException, MessagingException {

        String subject = Constants.BOOKING_SUCCESS_SUBJECT;
        String content = Constants.BOOKING_SUCCESS_CONTENT_KHU_HOI;

        content = content.replace("[[name]]", passenger.getFullName());
        content = content.replace("[[adult]]", ticketTo.getTotalAdult().toString());
        content = content.replace("[[children]]", ticketTo.getTotalChildren().toString());
        content = content.replace("[[baby]]", ticketTo.getTotalBaby().toString());
        content = content.replace("[[flightNo]]", scheduleTo.getFlightNo());
        content = content.replace("[[start]]", convertLocalDatetimeToString(scheduleTo.getStartTime()));
        content = content.replace("[[end]]", convertLocalDatetimeToString(scheduleTo.getEndTime()));
        content = content.replace("[[totalPrice]]", ticketTo.getTotalPrice().toString());

        content = content.replace("[[adult]]", ticketBack.getTotalAdult().toString());
        content = content.replace("[[children]]", ticketBack.getTotalChildren().toString());
        content = content.replace("[[baby]]", ticketBack.getTotalBaby().toString());
        content = content.replace("[[flightNoVe]]", scheduleBack.getFlightNo());
        content = content.replace("[[startVe]]", convertLocalDatetimeToString(scheduleBack.getStartTime()));
        content = content.replace("[[endVe]]", convertLocalDatetimeToString(scheduleBack.getEndTime()));
        content = content.replace("[[totalPriceVe]]", ticketBack.getTotalPrice().toString());

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

    private static boolean validateEmail(String email) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private Long totalPrice(Integer totalAdult, Integer totalChildren, Integer totalBaby, Long price) {
        long childrenPrice = price * 2/ 3;
        long babyPrice = price / 2;
        long totalPrice = price * totalAdult + totalChildren * childrenPrice + totalBaby * babyPrice;
        long tax = totalPrice / 10;
        return totalPrice + tax;

    }
}
