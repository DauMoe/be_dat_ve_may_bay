package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.users.Passenger;
import com.outsource.bookingticket.exception.ErrorException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.regex.Matcher;

@Service
@Transactional
public class PassengerService extends BaseService {

    public ResponseCommon editPassenger(Passenger passengerRequest) {

        if (passengerRequest != null && passengerRequest.getId() != null) {
            Passenger passengerSaved = clientRepository.findPassengerById(passengerRequest.getId());

            if (passengerSaved != null) {
                if (!validateEmail(passengerRequest.getEmail())) {
                    throw new ErrorException("Invalid Email");
                }

                passengerSaved.setEmail(passengerRequest.getEmail());
                passengerSaved.setFullName(passengerRequest.getFullName());
                passengerSaved.setPhoneNo(passengerRequest.getPhoneNo());

                clientRepository.save(passengerSaved);
            } else throw new ErrorException("Invalid Request");
        } else throw new ErrorException("Invalid Request");

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult("Edit success");
        return responseCommon;
    }

    private static boolean validateEmail(String email) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
