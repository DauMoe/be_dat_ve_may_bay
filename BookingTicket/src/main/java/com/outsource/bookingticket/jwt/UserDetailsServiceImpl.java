package com.outsource.bookingticket.jwt;

import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepo.getUserByEmail(username);

        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException("User not found " + username);
        }

        return new CustomUserDetails(user);
    }

    @Transactional
    public UserDetails loadUserById(Integer id) { // used for JWT filter
        UserEntity user = userRepo.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return new CustomUserDetails(user);
    }

}