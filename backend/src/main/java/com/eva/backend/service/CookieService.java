package com.eva.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.eva.backend.model.User;
import com.eva.backend.records.CookieEssentials;

@Service
public class CookieService {
    /* Class generating cookies containing accessToken (short duration) and refresh token (long duration) */
    private long accessTokenDurationInMilliSec = 1 * 60 * 1000;
    private long refreshTokenDurationInMilliSec = 7 * 24 * 3600 * 1000;

    @Autowired
    private JWTService jwtService;

    @Value("${app.cookie.secure:false}")  // false par défaut pour dev, devra être true en production
    private boolean cookieSecure;

    public CookieEssentials generateRefreshCookie(User user){
        return generateCookie(user, refreshTokenDurationInMilliSec, "jwt-refresh");
    }

    public CookieEssentials generateAccessCookie(User user){
        return generateCookie(user, accessTokenDurationInMilliSec, "jwt");
    }

    private CookieEssentials generateCookie(User user, long tokenDuration, String cookieName){
        String token = jwtService.generateToken(user.getUsername(), tokenDuration);
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                  .httpOnly(true) //empêche les attaques JS
                  .secure(cookieSecure)   //https
                  .path("/")      //accessible pour tout
                  .maxAge(tokenDuration) 
                  .sameSite("Strict") //protection csrf
                  .build();
        return new CookieEssentials(cookie.toString(),
                                    tokenDuration);
    }
}
