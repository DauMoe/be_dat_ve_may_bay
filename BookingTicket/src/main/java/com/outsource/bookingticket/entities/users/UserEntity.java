package com.outsource.bookingticket.entities.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uid")
    private String uid;

    @Column(name = "username", length = 128)
    private String username;

    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name ="password", length = 64, nullable = false)
    private String password;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name ="date_of_birth", length = 64)
    private LocalDate dateOfBirth;

    @Column(name ="gender")
    private Boolean gender;

    @Column(name ="phone", length = 10)
    private String phone;

    @Column(name ="role")
    private Boolean role;

    private boolean enabled;
}
