package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.common.TicketCommon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommonRepository extends JpaRepository<TicketCommon, Integer> {
    @Query(value = "select * from user u join ticket t on u.id = t.uid " +
            "join flight_schedule fc on t.flight_schedule_id = fc.flight_schedule_id " +
            "join flight f on fc.flight_no = f.flight_no " +
            "join airport_geo a on f.from_airport_id = a.airport_geo_id " +
            "join airplane ap on f.airplane_id = ap.airplane_id " +
            "where t.booking_state <> 'CANCELED' and fc.flight_schedule_id = ?1 ", nativeQuery = true)
    List<TicketCommon> findTicketByFlightScheduleId(Integer scheduleId);
}
