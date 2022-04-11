package com.outsource.bookingticket.utils;

import com.outsource.bookingticket.constants.Constants;
import com.outsource.bookingticket.dtos.commons.CommonList;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import com.outsource.bookingticket.dtos.commons.ResponseCommonList;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Helper {
    public static ResponseCommon createSuccessCommon(Object object) {
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(200);
        responseCommon.setResult(object);
        return responseCommon;
    }

    public static ResponseCommonList createSuccessListCommon(List<Object> objects) {
        ResponseCommonList responseCommon = new ResponseCommonList();
        CommonList commonList = new CommonList();
        responseCommon.setCode(200);

        commonList.setList(objects);
        responseCommon.setResult(commonList);
        return responseCommon;
    }

    public static void sendMailCommon(String[] addresses, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        MimeMessage message = mailSender.createMimeMessage(); // interface to create MIME
        MimeMessageHelper help = new MimeMessageHelper(message); // a class support create MIME with image,audio or html

        help.setFrom(Constants.MAIL_FROM, Constants.MAIL_SENDER_NAME);
        help.setTo((String[]) addresses);
        help.setSubject(subject);

        help.setText(content, true);
        mailSender.send(message);
    }
}
