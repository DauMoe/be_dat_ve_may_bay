package com.outsource.bookingticket.services;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.entities.users.PasswordResetToken;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.ErrorException;
import com.outsource.bookingticket.exception.PasswordResetTokenNotFoundException;
import com.outsource.bookingticket.repositories.PasswordResetTokenRepository;
import com.outsource.bookingticket.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
            // Nếu có passwordReset set lại thời hạn token
            passwordReset.setExpiryDate(new Date(System.currentTimeMillis() + Constants.EXPIRATION_DATE));
            passwordReset.setToken(token);
        } else {
            // Nếu không có passwordReset thì tạo cái mới
            passwordReset = new PasswordResetToken(token, user);
            passwordReset.setExpiryDate(new Date(System.currentTimeMillis() + Constants.EXPIRATION_DATE));
        }
        // Lưu vào database
        passwordResetTokenRepository.save(passwordReset);
    }

    // Hàm đăng kí user
    public void registerUser(UserEntity user) {
        // Để user chưa được kích hoạt
        user.setEnabled(true);
        user.setRole(true); // User role
        user.setUid(UUID.randomUUID().toString());
        encodePassword(user);
        // Tạo ra 1 token để xác thực ở email
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);

        // Lưu user vào database
        userRepository.save(user);
    }

    public String validatePasswordResetToken(String token) {
        // Lấy token lên
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        // Kiểm tra token còn tồn tại và còn hạn hay không
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    public UserEntity getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        return userRepository.findById(passwordResetToken.getUser().getId()).get();
    }

    // Đổi mật khẩu người dùng
    public void changePassword(UserEntity user, String newPassword) {
        System.out.println("Maajt khau moi " + newPassword );
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }

    public boolean checkIfValidOldPassword(UserEntity user, String oldPassword) {

        passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            return true;
        }

        return false;
    }

    // Xoá đối tượng password reset token
    public void deletePasswordResetToken(String token) throws PasswordResetTokenNotFoundException {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new PasswordResetTokenNotFoundException("Could not find passwordResetToken with id: " + passwordResetToken.getId());
        }

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    // Cập nhật thông tin người dùng
    public UserEntity updateAccount(UserEntity userInForm) {
        UserEntity userInDB = userRepository.getUserByEmail(userInForm.getEmail());
        // Nếu k tìm thấy người dùng
        if (userInDB == null) {
            throw new ErrorException(MessageUtil.USER_NOT_FOUND);
        }
        // Kiểm tra có cập nhật password ko -- Không dùng nữa
//        if (userInForm.getPassword() != null && !userInForm.getPassword().isEmpty()) {
//            userInDB.setPassword(userInForm.getPassword());
//            encodePassword(userInDB);
//        }
        // Cập nhật thông tin
        userInDB.setDateOfBirth(userInForm.getDateOfBirth());
        userInDB.setGender(userInForm.getGender());
        userInDB.setPhone(userInForm.getPhone());

        return userRepository.save(userInDB);
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
