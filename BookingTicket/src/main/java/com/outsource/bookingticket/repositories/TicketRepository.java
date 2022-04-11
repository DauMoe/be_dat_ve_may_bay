package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Ticket findBySeatNumberAndFlightScheduleId(String seatNumber, Integer flightScheduleId);

    Optional<Ticket> findTicketByTicketId(Integer ticketId);

    List<Ticket> findByUid(Integer uid);

    @Query(value = "select t.* from ticket t join flight_schedule fs on t.flight_schedule_id = fs.flight_schedule_id " +
            "where fs.flight_no = ?1 and BOOKING_STATE = 'BOOKED'", nativeQuery = true)
    List<Ticket> findTicketByFlightNoAndState(String flightNo);
}
