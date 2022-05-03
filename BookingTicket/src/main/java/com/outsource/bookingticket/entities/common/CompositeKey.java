package com.outsource.bookingticket.entities.common;

import java.io.Serializable;

public class CompositeKey implements Serializable {
    private Integer flightId;
    private Integer flightScheduleId;
    private Integer ticketId;
    private Integer airplaneId;
}
