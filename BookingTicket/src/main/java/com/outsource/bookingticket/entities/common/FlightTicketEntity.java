package com.outsource.bookingticket.entities.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@IdClass(CompositeKey.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightTicketEntity implements Serializable {

    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "flight_no")
    private String flightNo;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "weight_package")
    private Integer weightPackage;

    private Long price;

    @Id
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @Id
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Id
    @Column(name = "airplane_id")
    private Integer airplaneId;

    private String brand;

    @Column(name = "link_img_brand")
    private String linkImgBrand;
}
