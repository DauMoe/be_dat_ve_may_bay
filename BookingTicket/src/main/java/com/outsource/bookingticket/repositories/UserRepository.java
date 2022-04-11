package com.outsource.bookingticket.repositories;

import com.outsource.bookingticket.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1 OR u.username = ?1")
    UserEntity getUserByEmailOrUsername(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1 ")
    UserEntity getUserByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.verificationCode = ?1")
    UserEntity getUserByVerificationCode(String verificationCode);

    @Query("UPDATE UserEntity u SET u.enabled = true, u.verificationCode = null WHERE u.id = ?1")
    @Modifying
    void enable(Integer id);

    @Query("UPDATE UserEntity u SET u.password = ?2 WHERE u.id =?1")
    @Modifying
    int updatePassword(Integer id, String password);

    Optional<UserEntity> findUserEntityById(Integer id);

    List<UserEntity> findUserEntitiesByIdIn(List<Integer> ids);
}
