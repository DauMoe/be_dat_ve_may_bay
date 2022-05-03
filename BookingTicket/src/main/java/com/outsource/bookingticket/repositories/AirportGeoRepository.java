package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.airport.AirportGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportGeoRepository extends JpaRepository<AirportGeo, Integer> {
    List<AirportGeo> findAirportGeosByLocationId(Integer locationId);
}
