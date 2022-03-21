package com.outsource.bookingticket.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsource.bookingticket.dtos.commons.ResponseCommon;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthorityEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ObjectMapper objecMapper = new ObjectMapper();
        ResponseCommon responseCommon = new ResponseCommon();
        responseCommon.setCode(403);
        final String expired = (String) request.getAttribute("expired");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        if (expired!=null){
            responseCommon.setResult(expired);
            response.getWriter().write(objecMapper.writeValueAsString(responseCommon));
        }else{
            responseCommon.setResult("Invalid Login details");
            response.getWriter().write(objecMapper.writeValueAsString(responseCommon));
        }
    }
}
