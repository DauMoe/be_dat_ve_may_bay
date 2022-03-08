package com.outsource.bookingticket.entities.flight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "flight")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "flight_no", nullable = false, unique = true)
    private String flightNo;

    @Column(name = "from_airport_id", nullable = false)
    private Integer fromAirportId;

    @Column(name = "to_airport_id", nullable = false)
    private Integer toAirportId;

    @Column(name = "airplane_id", nullable = false)
    private Integer airplaneId;
}
