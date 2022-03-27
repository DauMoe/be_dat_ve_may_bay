package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.users.PasswordResetToken;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.PasswordResetTokenNotFoundException;

import com.outsource.bookingticket.pojo.PasswordResetTokenDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/forgot")
public class ForgotPasswordController extends BaseController {

    @CrossOrigin(maxAge = 3600, origins = "*")
    @GetMapping(value = "/reset-password")
    public String getPageResetPassword(@RequestParam String token) {
        // Kiểm tra token trong url ở gmail
        if (StringUtils.isNoneEmpty(token)) {
            String result = userService.validatePasswordResetToken(token);
            // Nếu không tồn tại hoặc hết hạn trả ra trang lỗi
            if (result != null) {
                return "error/404.html";
            }

            UserEntity user = userService.getUserByPasswordResetToken(token);
            if (user == null) {
                return "error/404.html";
            }
            // Trả về trang để người dùng đổi mật khẩu
            return "password-reset.html";
        }
        return "error/404.html";
    }

    @CrossOrigin(maxAge = 3600, origins = "*")
    @PostMapping(value = "/save-password", produces = "application/json")
    public ResponseEntity<?> savePassword(@RequestBody PasswordResetTokenDTO passwordDTO) throws PasswordResetTokenNotFoundException {
        // Kiểm tra token
        String result = userService.validatePasswordResetToken(passwordDTO.getToken());
        ResponseCommon responseCommon = new ResponseCommon();
        // Nếu token k có hoặc hét hạn trả lỗi
        if (result != null) {
            responseCommon.setCode(404);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }
        // Lấy user theo token
        UserEntity user = userService.getUserByPasswordResetToken(passwordDTO.getToken());
        // Nếu không tồn tại trả về lỗi
        if (user == null) {
            responseCommon.setCode(404);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.OK);
        }

        responseCommon.setCode(200);
        responseCommon.setResult("Reset password success!");
        userService.changePassword(user, passwordDTO.getNewPassword());
        // Delete PasswordReset, need fix
        userService.deletePasswordResetToken(passwordDTO.getToken());

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }
}
