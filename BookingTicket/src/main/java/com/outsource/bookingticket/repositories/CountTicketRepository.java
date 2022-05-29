package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.ticket.CountTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountTicketRepository extends JpaRepository<CountTicket, Integer> {

    @Query(value = "SELECT flight_schedule_id, ticket_type, COUNT(ticket_id) as total_ticket " +
            "FROM ticket " +
            "GROUP BY flight_schedule_id, ticket_type " +
            "ORDER BY flight_schedule_id", nativeQuery = true)
    List<CountTicket> findAllGroupBySchedule();
}
