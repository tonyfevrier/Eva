package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;

import com.eva.backend.model.User;
import com.eva.backend.records.CookieEssentials;
import com.eva.backend.records.TwoCookies;

public class CookieService {
    private long accessTokenDurationInMilliSec = 10 * 60 * 1000;
    private long refreshTokenDurationInMilliSec = 7 * 24 * 3600 * 1000;

    @Autowired
    private JWTService jwtService;

    /*public TwoCookies generateAccessAndRefreshCookie(User user){
        CookieEssentials accessCookie = generateAccessCookie(user);
        CookieEssentials refreshCookie = generateCookie(user, refreshTokenDurationInMilliSec, "jwt-refresh");
        return new TwoCookies(accessCookie, refreshCookie);
    }*/

    public CookieEssentials generateRefreshCookie(User user){
        return generateCookie(user, refreshTokenDurationInMilliSec, "jwt-refresh");
    }

    public CookieEssentials generateAccessCookie(User user){
        return generateCookie(user, accessTokenDurationInMilliSec, "jwt");
    }

    private CookieEssentials generateCookie(User user, long tokenDuration, String cookieName){
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                  .httpOnly(true) //empêche les attaques JS
                  .secure(true)   //https
                  .path("/")      //accessible pour tout
                  .maxAge(tokenDuration) 
                  .sameSite("Strict") //protection csrf
                  .build();
        return new CookieEssentials(cookie.toString(),
                                    tokenDuration);
    }
}
