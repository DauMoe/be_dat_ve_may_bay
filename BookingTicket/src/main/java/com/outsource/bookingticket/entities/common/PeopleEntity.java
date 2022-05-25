package com.outsource.bookingticket.entities.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeopleEntity implements Serializable {
    @Id
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @Column(name = "total_people")
    private Integer totalPeople;

    @Column(name = "available_seat")
    private Integer availableSeat;
}
