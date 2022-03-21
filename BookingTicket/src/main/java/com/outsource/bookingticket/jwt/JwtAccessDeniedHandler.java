package com.outsource.bookingticket.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objecMapper = new ObjectMapper();
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(403);
        responseCommon.setResult("Quyền truy cập bị từ chối");
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objecMapper.writeValueAsString(responseCommon));
    }
}
