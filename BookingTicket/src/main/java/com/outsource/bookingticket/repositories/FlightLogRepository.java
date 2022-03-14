package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.flight.FlightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightLogRepository extends JpaRepository<FlightLog, Integer> {
}
