package com.outsource.bookingticket.controllers;

import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.entities.users.PasswordResetToken;
import com.outsource.bookingticket.entities.users.UserEntity;
import com.outsource.bookingticket.exception.PasswordResetTokenNotFoundException;

import com.outsource.bookingticket.pojo.PasswordResetTokenDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/forgot")
public class ForgotPasswordController extends BaseController {

    @GetMapping(value = "/reset-password")
    public String getPageResetPassword(@RequestParam String token) {
        if (StringUtils.isNoneEmpty(token)) {
            String result = userService.validatePasswordResetToken(token);

            if (result != null) {
                return "error/404.html";
            }

            UserEntity user = userService.getUserByPasswordResetToken(token);
            if (user == null) {
                return "error/404.html";
            }

            return "password-reset.html";
        }
        return "error/404.html";
    }


    @PostMapping(value = "/save-password", produces = "application/json")
    public String savePassword(@RequestBody PasswordResetTokenDTO passwordDTO) throws PasswordResetTokenNotFoundException {
        String result = userService.validatePasswordResetToken(passwordDTO.getToken());

        if (result != null) {
            return "error/404.html";
        }

        UserEntity user = userService.getUserByPasswordResetToken(passwordDTO.getToken());
        if (user == null) {
            return "error/404.html";
        }

        userService.changePassword(user, passwordDTO.getNewPassword());
        // Delete PasswordReset, need fix
        userService.deletePasswordResetToken(passwordDTO.getToken());

        return "reset-success.html";
    }
}
