package com.outsource.bookingticket.jwt;

import com.outsource.bookingticket.entities.users.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private static final String ROLE = "ROLE_";

    private UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = Collections.singleton(user.getRole() ? "ADMIN" : "USER");
        List<SimpleGrantedAuthority> authories = new ArrayList<>();

        for (String role : roles) {
            authories.add(new SimpleGrantedAuthority(ROLE + role));
        }

        return authories;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { // Check user active?
        return user.isEnabled();
    }

}
