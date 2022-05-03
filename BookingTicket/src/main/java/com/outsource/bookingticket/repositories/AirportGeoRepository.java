package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.airport.AirportGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportGeoRepository extends JpaRepository<AirportGeo, Integer> {
}
