package com.outsource.bookingticket.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =  Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final String MAIL_FROM = "truongnv2026@gmail.com";

    public static final String MAIL_USERNAME = "truongnv2026@gmail.com";

    public static final String MAIL_PASSWORD = "jgltycmvkiotoowt";

    public static final String MAIL_HOST = "smtp.gmail.com";

    public static final int MAIL_PORT = 587;

    public static final String MAIL_SENDER_NAME = "Airpot KTMM";

    public static final String SMTP_AUTH = "true";

    public static final String SMTP_SECURED = "true";

    public static final String USER_VERIFY_SUBJECT = "Please verify your registration to continue using Account";

    public static final String USER_VERIFY_CONTENT="Dear [[name]],<br>Please click the link below to verify your registration:<br><h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>Thank you!";

    public static final String RESET_PASSWORD_SUBJECT = "Your password has been reset";

    public static final String RESET_PASSWORD_CONTENT = "Hi [[name]],<br>This is your new password: [[newPassword]]<br>Note: for security reason, you must change your password after logging in.<br>Thank you!";

    ////////// FOR RESET PASSWORD
    public static final long EXPIRATION_DATE = 86400000; // 1 Day expiry

    public static final String RESET_PASSWORD_WEB_SUBJECT = "A request to reset your password";

    public static final String RESET_PASSWORD_WEB_CONTENT ="Hi [[name]],<br> Someone has requested to reset your password with our project. "
            + "If it were not you, please ignore otherwise please click on the link below to set a new password: <br>"
            + "<a href=\"[[URL]]\">Click this link to Reset Password</a><br>Thank you!";
}
