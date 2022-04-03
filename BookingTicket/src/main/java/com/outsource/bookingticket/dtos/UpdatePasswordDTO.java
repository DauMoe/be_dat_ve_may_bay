package com.outsource.bookingticket.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePasswordDTO {

    private String oldPassword;

    private String newPassword;

    private String confirmPass;
}
