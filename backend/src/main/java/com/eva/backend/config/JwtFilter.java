package com.eva.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eva.backend.service.JWTService;
import com.eva.backend.service.MyUserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    private JWTService jwtService;

    @Autowired 
    private ApplicationContext context;

    private record  TokenInfo(String username, String token) {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            /* On récupère le token depuis la requête, on vérifie que ça matche
             * avec un utilisateur et si oui on crée un nouvel objet d'authentication
             */
            
            String path = request.getServletPath();
            if (path == null || path.isBlank()) {
                path = request.getRequestURI();
            }
            if (isPathAnAllUsersPermittedPath(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // for request with path necessitating an authentication to be accepted
            TokenInfo tokenInfo = getTokenAndeUsernameFrom(request);
            if (tokenInfo.username != null && !tokenInfo.username.isBlank()
                && tokenInfo.token != null && !tokenInfo.token.isBlank()
                && authenticationObjectDoesNotExist()){
                createNewAuthenticationObject(tokenInfo, request);
            }
            filterChain.doFilter(request, response);
    }

    private boolean isPathAnAllUsersPermittedPath(String path){
        return path.equals("/auth/register") 
            || path.equals("/auth/confirm") 
            || path.equals("/auth/login") 
            || path.equals("/auth/logout")
            || path.equals("/auth/refresh")
            || path.equals("/auth/recoverPwd")
            || path.equals("/auth/resetMail")
            || path.equals("/expe/getAll")
            || path.startsWith("/expe/get/")
            || path.startsWith("/pdf/getPdfs")
            || path.startsWith("/pdf/getPdf/");
    }

    private TokenInfo getTokenAndeUsernameFrom(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        // Utile si on stockait le token dans le local storage
        if (authHeader != null && authHeader.startsWith("Bearer")){
            return getTokenAndeUsernameFromAuthorization(authHeader);
        }

        // Adapté au cookie http.only utilisé dans mon cas
        if (token == null && request.getCookies() != null) {
            return getTokenAndeUsernameFromCookie(request);
        }
        return new TokenInfo(username, token);
    }

    private TokenInfo getTokenAndeUsernameFromAuthorization(String authHeader){
        String token = authHeader.substring(7); // token is after Bearer in the string authHeader
        return new TokenInfo(extractUsernameSafely(token), token);
    }
    private TokenInfo getTokenAndeUsernameFromCookie(HttpServletRequest request){
        String token = null;
        String username = null;
        for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    username = extractUsernameSafely(token);
                }
            }
        return new TokenInfo(username, token);
    }

    private String extractUsernameSafely(String token){
        /* Un token expiré ou invalide ne doit pas faire échouer la requête avec une 500 :
           on laisse la chaîne de filtres répondre un 401 que le front pourra rejouer après refresh. */
        try {
            return jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private boolean authenticationObjectDoesNotExist(){
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
    private void createNewAuthenticationObject(TokenInfo tokenInfo, HttpServletRequest request){
        /* vérifie si les infos liées au token sont les mêmes que celles de la base de données,
         et crée l'objet authentication si c'est le cas */
        UserDetails userDetails;
        try {
            userDetails = getUserFromDatabase(tokenInfo.username);
        } catch (UsernameNotFoundException e) {
            return;
        }

        if (isTokenValid(tokenInfo.token, userDetails)){
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);;
        }
    }
    
    private UserDetails getUserFromDatabase(String username){
        return context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
    }

    private boolean isTokenValid(String token, UserDetails userDetails){
        try {
            return jwtService.validateToken(token, userDetails);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
}
