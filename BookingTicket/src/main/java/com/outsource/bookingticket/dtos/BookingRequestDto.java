package com.outsource.bookingticket.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private String namePassenger;
    private String phoneNumber;
    private String email;
    private Integer ticketIdTo;
    private Integer ticketIdBack;

}
