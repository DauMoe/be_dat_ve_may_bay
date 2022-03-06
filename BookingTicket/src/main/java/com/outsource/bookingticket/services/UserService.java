package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.users.UserEntity;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@Log4j2
@Service
@Transactional
public class UserService extends BaseService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean exitUserByEmail(String email) {
        if (!validateEmail(email)) {
            return false;
        }

        return userRepository.existsByEmail(email);
    }

    public void registerUser(UserEntity user) {
        encodePassword(user);
        user.setEnabled(false);
        user.setRole(false); // User role

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        userRepository.save(user);
    }

    public boolean verifyCode(String verificationCode) {
        UserEntity user = userRepository.getUserByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        }
        else {
            userRepository.enable(user.getId());
            return true;
        }
    }

    private static boolean validateEmail(String email) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private void encodePassword(UserEntity user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
