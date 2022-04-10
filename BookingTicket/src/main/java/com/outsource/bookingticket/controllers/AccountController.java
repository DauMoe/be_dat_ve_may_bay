package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.UpdatePasswordDTO;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.jwt.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/account")
public class AccountController extends BaseController {

    // Lấy thông tin cho người dùng đang đăng nhập
    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping( produces = "application/json")
    public ResponseEntity<?> viewDetail(@AuthenticationPrincipal CustomUserDetails loggerUser) {
        String email = loggerUser.getUsername(); // Lấy email ở đối tượng spring security Context
        UserEntity user = userService.getUserByEmail(email);

        ResponseCommon responseCommon = new ResponseCommon();
        // Nếu ko có trả về error
        if (user == null) {
            responseCommon.setCode(404);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }

        responseCommon.setCode(200);
        responseCommon.setResult(user);
        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    // Cập nhật thông tin người dùng
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/update-info", produces = "application/json")
    public ResponseEntity<?> saveDetail(@RequestBody  UserEntity userEntity,
                                        @AuthenticationPrincipal CustomUserDetails loggerUser) throws IOException {
        UserEntity savedUser  = userService.updateAccount(userEntity);

        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(savedUser);
        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }

    // Cập nhật mật khẩu người dùng
    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/update-password", produces = "application/json")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO password,
                                            @AuthenticationPrincipal CustomUserDetails loggerUser) {
        ResponseCommon responseCommon = new ResponseCommon();
        if (!password.getConfirmPass().equals(password.getNewPassword())) {
            responseCommon.setCode(400);
            responseCommon.setResult("Mật khẩu xác nhận không hợp lệ");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }
        // Kiểm tra mật khẩu cũ có đúng ko mới cho đổi
        if (userService.checkIfValidOldPassword(loggerUser.getUser(), password.getOldPassword())) {
            userService.changePassword(loggerUser.getUser(), password.getNewPassword());
            responseCommon.setResult("Đổi mật khẩu thành công");
            responseCommon.setCode(200);
        } else {
            responseCommon.setResult("Đổi mật không thành công");
            responseCommon.setCode(400);
        }

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }
}
