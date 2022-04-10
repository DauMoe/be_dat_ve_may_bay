package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.airplane.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Integer> {
}
