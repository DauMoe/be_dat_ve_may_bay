package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Integer> {
    List<FlightSchedule> findFlightSchedulesByFlightNo(String flightNo);
}
