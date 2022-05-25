package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.airport.AirportGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportGeoRepository extends JpaRepository<AirportGeo, Integer> {
    List<AirportGeo> findAirportGeosByLocationId(Integer locationId);

    Optional<AirportGeo> findByAirportNameAndLocationId(String airportName, Integer locationId);
}
