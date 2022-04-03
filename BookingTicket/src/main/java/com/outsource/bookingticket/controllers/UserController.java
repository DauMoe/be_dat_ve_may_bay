package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.UserDTO;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.jwt.CustomUserDetails;
import com.outsource.bookingticket.jwt.JwtTokenProvider;
import com.outsource.bookingticket.pojo.LoginRequest;
import com.outsource.bookingticket.pojo.PasswordResetDTO;
import com.outsource.bookingticket.pojo.SignupRequest;
import com.outsource.bookingticket.utils.MailUtil;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description This class use include API for user
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Validate username and password using spring authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // If dont have exception -> set information to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gerenate JWT token and return to user
        String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(customUserDetails.getUser().getId());
        userDTO.setToken(jwt);
        userDTO.setRole(roles.get(0));
        userDTO.setUsername(customUserDetails.getUser().getUsername());
        // Tạo response trả dữ liệu cho phía client
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(userDTO);

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signupRequest, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        // đăng ký người dùng
        ResponseCommon responseCommon = new ResponseCommon();
        // Gọi tới hàm kiểm tra email hợp và tồn tại không
        if (userService.exitUserByEmail(signupRequest.getEmail())) {
            responseCommon.setCode(204);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(signupRequest.getUsername());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(signupRequest.getPassword());
        // Gọi hàm tạo user
        userService.registerUser(userEntity);
        // Gọi hàm gửi mail để xác thực tài khoản người dùng
        sendVerificationEmail(request, userEntity);

        responseCommon.setCode(200);
        responseCommon.setResult("Registration success");

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/verify", produces = "application/json")
    public ResponseEntity<?> verifyAccount(@RequestParam(name = "code") String code) {
        // Dùng để xác thực token ở email, gọi tới hàm xác thực
        boolean verified = userService.verifyCode(code);
        String result = verified ? "Congratulations! Your account has been verified." : "Your account was already verified, or the verification code is invalid";

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(result);

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    // Sử dụng để tạo email gửi URL để lấy page đổi mật khẩu
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/password-reset-token", produces = "application/json")
    public ResponseEntity<?> resetPasswordToken(HttpServletRequest request, @RequestBody PasswordResetDTO passwordResetDTO) throws MessagingException, UnsupportedEncodingException {
        UserEntity user = userService.getUserByEmail(passwordResetDTO.getEmail());
        ResponseCommon responseCommon = new ResponseCommon();
        // Nếu user k tồn tại
        if (user == null) {
            responseCommon.setCode(404);
            responseCommon.setResult("Your email does not exist");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }

        // tạo ra PasswordResetToken
        String token = RandomString.make(64);
        userService.createPasswordResetTokenForUser(token, user);
        // Gửi URL vào email
        sendURLPasswordResetToken(request, token, user);
        responseCommon.setCode(200);
        responseCommon.setResult("Your link reset password has been send. Please check your e-mail.");
        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    private void sendVerificationEmail(HttpServletRequest request, UserEntity user) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Constants.USER_VERIFY_SUBJECT;
        String content = Constants.USER_VERIFY_CONTENT;

        MimeMessage message = mailSender.createMimeMessage(); // interface to create MIME
        MimeMessageHelper help = new MimeMessageHelper(message); // a class support create MIME with image,audio or html

        help.setFrom(Constants.MAIL_FROM, Constants.MAIL_SENDER_NAME);
        help.setTo(toAddress);
        help.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());

        String verifyURL = MailUtil.getSiteURL(request, "/create") + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        help.setText(content, true);
        mailSender.send(message);
    }

    private void sendURLPasswordResetToken(HttpServletRequest request, String token, UserEntity user) throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Constants.RESET_PASSWORD_WEB_SUBJECT;
        String content = Constants.RESET_PASSWORD_WEB_CONTENT;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(Constants.MAIL_FROM, Constants.MAIL_SENDER_NAME);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());

        String resetURL = MailUtil.getSiteURL(request, "/api/user/password-reset-token") + "/api/forgot/reset-password?token=" + token;
        content = content.replace("[[URL]]", resetURL);

        helper.setText(content, true);
        mailSender.send(message);
    }
}
