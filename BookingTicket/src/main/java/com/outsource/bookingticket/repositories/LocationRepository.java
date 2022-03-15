package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findLocationsByLocationIdIn(Set<Integer> locationIds);
}
