package com.outsource.bookingticket.entities.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Table(name = "location")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "country_code")
    private Integer countryCode;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "country_name")
    private String countryName;

    private Long longitude;

    private Float latitude;
}
