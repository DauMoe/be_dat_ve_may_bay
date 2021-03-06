package com.outsource.bookingticket.entities.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Table(name = "flight_log")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_log_id", nullable = false, unique = true)
    private Integer flightLogId;

    @Column(name = "log_date")
    private LocalDateTime logDate;

    private String username;

    @Column(name = "flight_id", nullable = false)
    private Integer flightId;

    @Column(name = "flight_no_old")
    private String flightNoOld;

    @Column(name = "flight_no_new")
    private String flightNoNew;
}
