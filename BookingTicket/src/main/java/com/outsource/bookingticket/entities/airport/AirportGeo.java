package com.outsource.bookingticket.entities.airport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "airport_geo")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AirportGeo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airport_geo_id")
    private Integer airportGeoId;

    @Column(name = "airport_name")
    private String airportName;

    private String description;

    @Column(name = "location_id", nullable = false)
    private Integer locationId;
}
