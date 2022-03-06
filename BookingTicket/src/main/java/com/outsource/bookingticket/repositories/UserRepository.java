package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1")
    UserEntity getUserByEmail( String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.verificationCode = ?1")
    UserEntity getUserByVerificationCode(String verificationCode);

    @Query("UPDATE UserEntity u SET u.enabled = true, u.verificationCode = null WHERE u.id = ?1")
    @Modifying
    void enable(Integer id);
}
