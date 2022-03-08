package com.outsource.bookingticket.dtos;

import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private Integer flightScheduleId;
    private String seatNumber;
    private Long price;
    private BOOKINGSTATE bookingState;

}
