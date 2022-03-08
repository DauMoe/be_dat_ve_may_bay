package com.outsource.bookingticket.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(ErrorException.class)
//    public ResponseEntity<?> errorException(ErrorException error, WebRequest request) {
//        ResponseDTO response = new ResponseDTO(
//                "",
//                error.getMessage(),
//                HttpStatus.BAD_REQUEST.value(),
//                ((ServletWebRequest) request).getRequest().getServletPath());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(JwtTokenException.class)
//    public ResponseEntity<?> jwtTokenException(ErrorException error, WebRequest request) {
//        ResponseDTO response = new ResponseDTO(
//                "",
//                error.getMessage(),
//                HttpStatus.UNAUTHORIZED.value(),
//                ((ServletWebRequest) request).getRequest().getServletPath());
//        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//    }
}
