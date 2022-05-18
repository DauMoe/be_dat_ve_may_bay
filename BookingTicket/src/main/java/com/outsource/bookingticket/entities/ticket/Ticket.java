package com.outsource.bookingticket.entities.ticket;

import com.outsource.bookingticket.entities.enums.BOOKINGSTATE;
import com.outsource.bookingticket.entities.enums.TICKETTYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @Column(name = "row_seat")
    private String rowSeat;

    @Column(name = "price")
    private Long price;

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "weight_package")
    private Integer weightPackage;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private TICKETTYPE ticketType;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_state")
    private BOOKINGSTATE bookingState;

    @Column(name = "total_adult")
    private Integer totalAdult;

    @Column(name = "total_children")
    private Integer totalChildren;

    @Column(name = "total_baby")
    private Integer totalBaby;

    @Column(name = "total_price")
    private Long totalPrice;
}
