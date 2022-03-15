package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.users.PasswordResetToken;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.PasswordResetTokenNotFoundException;
import com.outsource.bookingticket.repositories.PasswordResetTokenRepository;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;

@Log4j2
@Service
@Transactional
public class UserService extends BaseService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public boolean exitUserByEmail(String email) {
        if (!validateEmail(email)) {
            return false;
        }

        return userRepository.existsByEmail(email);
    }

    public UserEntity getUserByEmail(String email) {
        if (!validateEmail(email)) {
            return null;
        }

        return userRepository.getUserByEmail(email);
    }

    public void createPasswordResetTokenForUser(String token, UserEntity user) {
        PasswordResetToken passwordReset = passwordResetTokenRepository.findByUserId(user.getId());

        if (passwordReset != null) {
            passwordReset.setExpiryDate(new Date(System.currentTimeMillis() + Constants.EXPIRATION_DATE));
            passwordReset.setToken(token);
        } else {
            passwordReset = new PasswordResetToken(token, user);
            passwordReset.setExpiryDate(new Date(System.currentTimeMillis() + Constants.EXPIRATION_DATE));
        }

        passwordResetTokenRepository.save(passwordReset);
    }

    public void registerUser(UserEntity user) {
        user.setEnabled(false);
        user.setRole(false); // User role
        user.setUid(UUID.randomUUID().toString());
        encodePassword(user);
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

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    public UserEntity getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        return userRepository.findById(passwordResetToken.getUser().getId()).get();
    }

    public void changePassword(UserEntity user, String newPassword) {

        userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }

    public void deletePasswordResetToken(String token) throws PasswordResetTokenNotFoundException {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new PasswordResetTokenNotFoundException("Could not find passwordResetToken with id: " + passwordResetToken.getId());
        }

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    private static boolean validateEmail(String email) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private void encodePassword(UserEntity user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    private static boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private static boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        final Calendar cal = Calendar.getInstance();

        return passwordResetToken.getExpiryDate().before(cal.getTime());
    }
}
