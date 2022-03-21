package com.outsource.bookingticket.dtos;

import lombok.Data;

@Data
public class UserDTO {

    private String token;

    private Integer userId;

    private String role;

    private String username;
}
