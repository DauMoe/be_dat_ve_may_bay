package com.outsource.bookingticket.entities.common;

import com.outsource.bookingticket.entities.enums.TICKETTYPE;

import java.io.Serializable;

public class CompositeKey2 implements Serializable {
    private Integer flightScheduleId;
    private TICKETTYPE ticketType;
}
