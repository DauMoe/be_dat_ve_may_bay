package com.outsource.bookingticket.utils;

import com.outsource.bookingticket.constants.Constants;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class MailUtil {

    public  static String getSiteURL(HttpServletRequest request, String path) {
        String siteURL = request.getRequestURL().toString();
        System.out.println("quang " + siteURL);
        return siteURL.replace(path, "");
    }

    public static JavaMailSenderImpl prepareMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(Constants.MAIL_HOST);
        mailSender.setPort(Constants.MAIL_PORT);
        mailSender.setUsername(Constants.MAIL_USERNAME);
        mailSender.setPassword(Constants.MAIL_PASSWORD);

        // Set mail propertie
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", Constants.SMTP_AUTH);
        properties.setProperty("mail.smtp.starttls.enable", Constants.SMTP_SECURED);

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
