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
    public ResponseEntity<?> savePassword(@RequestBody PasswordResetTokenDTO passwordDTO) throws PasswordResetTokenNotFoundException {
        String result = userService.validatePasswordResetToken(passwordDTO.getToken());
        ResponseCommon responseCommon = new ResponseCommon();
        if (result != null) {
            responseCommon.setCode(404);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.NOT_FOUND);
        }

        UserEntity user = userService.getUserByPasswordResetToken(passwordDTO.getToken());
        if (user == null) {
            responseCommon.setCode(404);
            responseCommon.setResult("There has error!");
            return new ResponseEntity<>(responseCommon, HttpStatus.NOT_FOUND);
        }

        responseCommon.setCode(200);
        responseCommon.setResult("Reset password success!");
        userService.changePassword(user, passwordDTO.getNewPassword());
        // Delete PasswordReset, need fix
        userService.deletePasswordResetToken(passwordDTO.getToken());

        return new ResponseEntity<>(responseCommon, HttpStatus.OK);
    }
}
