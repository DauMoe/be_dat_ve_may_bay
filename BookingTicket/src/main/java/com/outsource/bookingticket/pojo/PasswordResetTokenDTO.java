package com.outsource.bookingticket.pojo;

import lombok.Data;

@Data
public class PasswordResetTokenDTO {
    private String oldPassword;

    private  String token;

    private String newPassword;
}
