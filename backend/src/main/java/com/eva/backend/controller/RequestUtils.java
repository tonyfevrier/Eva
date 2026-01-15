package com.eva.backend.controller;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestUtils {

    public RequestUtils(){
    }

    public String getTokenFromRequest(HttpServletRequest request, String tokenName){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
