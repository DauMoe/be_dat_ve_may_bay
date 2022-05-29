package com.outsource.bookingticket.entities.ticket;

import com.outsource.bookingticket.entities.common.CompositeKey2;
import com.outsource.bookingticket.entities.enums.TICKETTYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@IdClass(CompositeKey2.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CountTicket implements Serializable {

    @Id
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private TICKETTYPE ticketType;

    @Column(name = "total_ticket")
    private Integer totalTicket;
}
