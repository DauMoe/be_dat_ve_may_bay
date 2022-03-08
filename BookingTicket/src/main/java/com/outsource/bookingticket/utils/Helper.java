package com.outsource.bookingticket.utils;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;

public class Helper {
    public static ResponseCommon createSucessCommon(Object object) {
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(object);
        return responseCommon;
    }
}
