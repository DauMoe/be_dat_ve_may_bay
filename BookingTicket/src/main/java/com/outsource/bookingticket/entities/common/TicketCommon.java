package com.outsource.bookingticket.entities.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommon implements Serializable {

    @Id
    @Column(name = "ticket_id")
    private Integer ticketId;
    @Column(name = "row_seat")
    private String rowSeat;
    private Long price;
    @Column(name = "booking_state")
    private String bookingState;
    @Column(name = "airplane_name")
    private String airplaneName;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "available_seat")
    private Integer availableSeat;
    @Column(name = "flight_id")
    private Integer flightId;
    @Column(name = "flight_no")
    private String flightNo;
    @Column(name = "from_airport_id")
    private Integer fromAirportId;
    @Column(name = "to_airport_id")
    private Integer toAirportId;
    @Column(name = "airplane_id")
    private Integer airplaneId;
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;
    private String username;
}
