package com.outsource.bookingticket.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private String namePassenger;
    private String phoneNumber;
    private String email;
    private Integer ticketIdTo;
    private Integer ticketIdBack;
    private Integer totalAdult = 1;
    private Integer totalChildren = 0;
    private Integer totalBaby = 0;

}
