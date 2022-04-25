package com.outsource.bookingticket.entities.enums;

import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.utils.MessageUtil;

import java.util.Arrays;

public enum TICKETTYPE {
    FIRST_CLASS("FIRST_CLASS", "FIRST"),
    BUSINESS_CLASS("BUSINESS_CLASS", "BUSINESS"),
    PREMIUM_CLASS("PREMIUM_CLASS", "PREMIUM"),
    ECONOMY_CLASS("ECONOMY_CLASS", "ECO");

    public final String input;
    public final String value;

    TICKETTYPE(String input, String value) {
        this.input = input;
        this.value = value;
    }

    public static TICKETTYPE getValue(String ticketType) {
        return Arrays.stream(TICKETTYPE.values())
                .filter(value -> value.input.equals(ticketType))
                .findFirst()
                .orElseThrow(() -> { throw new ErrorException(MessageUtil.SOME_ERRORS);
        });
    }
}
