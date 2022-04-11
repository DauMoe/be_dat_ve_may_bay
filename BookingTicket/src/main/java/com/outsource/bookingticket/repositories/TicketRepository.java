package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.ticket.Ticket;
import org.hibernate.annotations.Parameter;
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

}
