package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.flight_schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query(value = "SELECT * FROM flight_schedule fs " +
            "JOIN flight f ON fs.flight_no = f.flight_no", nativeQuery = true)
    List<Schedule> findAllSchedule();

    @Query(value = "SELECT * FROM flight_schedule fs " +
            "JOIN flight f ON fs.flight_no = f.flight_no " +
            "WHERE fs.start_time >= :start " +
            "AND fs.end_time <= :end ", nativeQuery = true)
    List<Schedule> findAllScheduleByTime(@Param("start") String start,
                                         @Param("end") String end);

    @Query(value = "SELECT * FROM flight_schedule fs " +
            "JOIN flight f ON fs.flight_no = f.flight_no " +
            "WHERE f.from_airport_id = :from " +
            "AND f.to_airport_id = :to ", nativeQuery = true)
    List<Schedule> findAllScheduleByLocation(@Param("from") Integer from,
                                         @Param("to") Integer to);

    @Query(value = "SELECT * FROM flight_schedule fs " +
            "JOIN flight f ON fs.flight_no = f.flight_no " +
            "WHERE fs.start_time >= :start " +
            "AND fs.end_time <= :end " +
            "AND f.from_airport_id = :from " +
            "AND f.to_airport_id = :to ", nativeQuery = true)
    List<Schedule> findAllScheduleByTimeAndLocation(@Param("start") String start, @Param("end") String end,
                                                    @Param("from") Integer from, @Param("to") Integer to);
}
