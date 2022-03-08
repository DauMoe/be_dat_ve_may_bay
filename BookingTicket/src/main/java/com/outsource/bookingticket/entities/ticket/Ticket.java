package com.outsource.bookingticket.entities.ticket;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;
    @Column(name = "seat_number")
    private String seatNumber;
    @Column(name = "price")
    private Long price;
    @Column(name = "uid")
    private Integer uid;
}
