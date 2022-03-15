package com.outsource.bookingticket.utils;

import com.outsource.bookingticket.dtos.commons.CommonList;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.dtos.commons.ResponseCommonList;

import java.util.List;

public class Helper {
    public static ResponseCommon createSuccessCommon(Object object) {
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(object);
        return responseCommon;
    }

    public static ResponseCommonList createSuccessListCommon(List<Object> objects) {
        ResponseCommonList responseCommon = new ResponseCommonList();
        CommonList commonList = new CommonList();
        responseCommon.setCode(200);

        commonList.setList(objects);
        responseCommon.setResult(commonList);
        return responseCommon;
    }
}
