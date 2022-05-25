package com.outsource.bookingticket.exception;

import com.outsource.bookingticket.dtos.commons.ResponseErrorCommon;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<?> errorException(ErrorException error, WebRequest request) {
        ResponseErrorCommon response = new ResponseErrorCommon(
                HttpStatus.BAD_REQUEST.value(),
                error.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public ResponseEntity<?> errorException(PasswordResetTokenNotFoundException error, WebRequest request) {
        ResponseErrorCommon response = new ResponseErrorCommon(
                HttpStatus.BAD_REQUEST.value(),
                error.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> npeException(NullPointerException error, WebRequest request) {
        ResponseErrorCommon response = new ResponseErrorCommon(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
