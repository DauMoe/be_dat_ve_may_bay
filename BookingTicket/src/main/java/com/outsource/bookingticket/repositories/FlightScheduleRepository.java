package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.enums.FLIGHTSTATE;
import com.outsource.bookingticket.entities.flight_schedule.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Integer> {
    List<FlightSchedule> findFlightSchedulesByFlightNo(String flightNo);
    Optional<FlightSchedule> findFlightSchedulesByFlightScheduleId(Integer flightScheduleId);

    List<FlightSchedule> findByAvailableSeat(Integer availableSeat);
    List<FlightSchedule> findAllByFlightNoInAndFlightState(List<String> flightNoList, FLIGHTSTATE flightState);
}
