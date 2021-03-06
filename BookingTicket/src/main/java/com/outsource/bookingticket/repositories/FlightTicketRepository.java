package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.common.FlightTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightTicketRepository extends JpaRepository<FlightTicketEntity, Integer> {

    @Modifying
    @Query(value = "SELECT f.flight_id, f.flight_no, fs.start_time, fs.end_time, f.from_airport_id, " +
            "f.to_airport_id, fs.flight_schedule_id, t.ticket_id, t.weight_package, t.price, a.brand, a.link_img_brand, a.airplane_id " +
            "FROM flight f " +
            "JOIN flight_schedule fs " +
            "ON f.flight_no = fs.flight_no " +
            "JOIN ticket t " +
            "ON t.flight_schedule_id = fs.flight_schedule_id " +
            "JOIN airplane a " +
            "ON f.airplane_id = a.airplane_id " +
            "WHERE fs.flight_state = 'FLIGHT_ON' " +
            "AND t.booking_state = 'AVAILABLE' " +
            "AND fs.available_seat >= :totalPeople " +
            "AND f.from_airport_id = :from " +
            "AND f.to_airport_id = :to " +
            "AND fs.start_time LIKE :time% ", nativeQuery = true)
    List<FlightTicketEntity> findAllFlightByLocationAndTime(@Param("time") String time,
                                                           @Param("from") Integer fromAirport,
                                                           @Param("to") Integer toAirport,
                                                           @Param("totalPeople") Integer totalPeople);

    @Modifying
    @Query(value = "SELECT f.flight_id, f.flight_no, fs.start_time, fs.end_time, f.from_airport_id, " +
            "f.to_airport_id, fs.flight_schedule_id, t.ticket_id, t.weight_package, t.price, a.brand, a.link_img_brand, a.airplane_id " +
            "FROM flight f " +
            "JOIN flight_schedule fs " +
            "ON f.flight_no = fs.flight_no " +
            "JOIN ticket t " +
            "ON t.flight_schedule_id = fs.flight_schedule_id " +
            "JOIN airplane a " +
            "ON f.airplane_id = a.airplane_id " +
            "WHERE fs.flight_state = 'FLIGHT_ON' " +
            "AND t.booking_state = 'AVAILABLE' " +
            "ORDER BY t.price ASC " +
            "LIMIT 6 ", nativeQuery = true)
    List<FlightTicketEntity> findAllFlightSuggestion();
}
