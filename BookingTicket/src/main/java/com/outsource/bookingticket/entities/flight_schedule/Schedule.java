package com.outsource.bookingticket.entities.flight_schedule;

import com.outsource.bookingticket.entities.enums.FLIGHTSTATE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Schedule implements Serializable {

    @Id
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "flight_no")
    private String flightNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_state")
    private FLIGHTSTATE flightState;

    @Column(name = "from_airport_id")
    private Integer fromAirportId;

    @Column(name = "to_airport_id")
    private Integer toAirportId;

    @Column(name = "airplane_id")
    private Integer airplaneId;
}
