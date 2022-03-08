package com.outsource.bookingticket.entities.flight_schedule;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "flight_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class FlightSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "flight_no")
    private String flightNo;
    @Column(name = "available_seat")
    private Integer availableSeat;

}
