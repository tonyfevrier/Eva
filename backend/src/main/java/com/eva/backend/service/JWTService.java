package com.eva.backend.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JWTService {
    /* Service de génération de JWT token */
    private String secretKey;

    @Value("${jwt.secret.key:#{null}}")
    private String envKey;

    @PostConstruct
    public void init(){
        /* Construction et encodage d'une clé pour signer le token */
        // Utilise la clé fixe depuis application.properties (ne pas régénérer à chaque restart)
        try{
            if (envKey != null && !envKey.isEmpty()) {
                secretKey = envKey;
            } else {
                // Fallback pour développement: génère une clé stable
                KeyGenerator keyGen= KeyGenerator.getInstance("HmacSHA256");
                SecretKey sk = keyGen.generateKey();
                secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
                System.err.println("[WARN] jwt.secret.key not set in properties. Using generated key for dev only.");
            }
        } catch (NoSuchAlgorithmException e){
            System.err.println(e);
        }
    }
    
    public String generateToken(String username, long tokenDurationInMilliSec){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                    .claims()
                    .add(claims)
                    .subject(username)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + tokenDurationInMilliSec))
                    .and()
                    .signWith(getKey())
                    .compact();
    }

    private SecretKey getKey() {
        /* Décodage de la clé */
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
