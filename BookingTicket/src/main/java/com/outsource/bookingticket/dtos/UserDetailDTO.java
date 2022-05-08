package com.outsource.bookingticket.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetailDTO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
}
