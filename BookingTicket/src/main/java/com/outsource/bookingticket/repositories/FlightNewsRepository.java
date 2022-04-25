package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.flight_news.FlightNews;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightNewsRepository extends PagingAndSortingRepository<FlightNews, Integer> {

    Long countByFlightNewsId(Integer id);
}
