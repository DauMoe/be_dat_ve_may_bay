package com.outsource.bookingticket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordResetTokenNotFoundException extends RuntimeException{
    public PasswordResetTokenNotFoundException(String message) {
        super(message);
    }
}
