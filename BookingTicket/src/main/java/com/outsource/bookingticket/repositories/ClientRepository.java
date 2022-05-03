package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Passenger, Integer> {

    Optional<Passenger> findClientByPhoneNo(String phoneNo);
}
