package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.users.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);

    Long countById(Integer id);

    PasswordResetToken findByUserId(Integer id);
}
