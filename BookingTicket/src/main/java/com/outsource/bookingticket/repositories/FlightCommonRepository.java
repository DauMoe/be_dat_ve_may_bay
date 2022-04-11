package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.common.FlightCommon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightCommonRepository extends JpaRepository<FlightCommon, Integer> {

    @Query(value = "select f.flight_id, f.flight_no, f.from_airport_id, f.to_airport_id, fs.start_time, fs.end_time, fs.available_seat, a.airplane_name " +
            "from flight f join flight_schedule fs on " +
            "f.flight_no = fs.flight_no " +
            "join airplane a on " +
            "f.airplane_id = a.airplane_id", nativeQuery = true)
    List<FlightCommon> findAllFlight();

}
