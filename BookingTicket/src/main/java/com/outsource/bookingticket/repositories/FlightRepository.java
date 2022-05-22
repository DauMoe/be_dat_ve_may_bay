package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.flight.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {
    Optional<FlightEntity> findFlightEntityByFlightId(Integer flightId);
    Optional<FlightEntity> findFlightEntityByFlightNo(String flightNo);
    List<FlightEntity> findByFromAirportIdInOrToAirportIdIn(List<Integer> airportGeoListFrom, List<Integer> airportGeoListTo);
    List<FlightEntity> findAllByAirplaneId(Integer airplaneId);
}
