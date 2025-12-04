package com.eva.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eva.backend.service.JWTService;
import com.eva.backend.service.MyUserDetailsService;

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
            
            String path = request.getRequestURI();
            if (path.equals("/api/register") || path.equals("/api/login")) {
                filterChain.doFilter(request, response);
                return;
            }
            TokenInfo tokenInfo = getTokenAndeUsernameFrom(request);
            if (tokenInfo.username != null && authenticationObjectDoesNotExist()){
                createNewAuthenticationObject(tokenInfo, request);
            }
            filterChain.doFilter(request, response);
    }

    private TokenInfo getTokenAndeUsernameFrom(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer")){
            return getTokenAndeUsernameFromAuthorization(authHeader);
        }

        if (token == null && request.getCookies() != null) {
            return getTokenAndeUsernameFromCookie(request);
        }
        return new TokenInfo(username, token);
    }

    private TokenInfo getTokenAndeUsernameFromAuthorization(String authHeader){
        String token = authHeader.substring(7); // token is after Bearer in the string authHeader
        String username = jwtService.extractUsername(token);
        return new TokenInfo(username, token);
    }
    private TokenInfo getTokenAndeUsernameFromCookie(HttpServletRequest request){
        String token = "";
        String username = "";
        for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    username = jwtService.extractUsername(token);
                }
            }
        return new TokenInfo(username, token);
    }

    private boolean authenticationObjectDoesNotExist(){
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
    private void createNewAuthenticationObject(TokenInfo tokenInfo, HttpServletRequest request){
        /* vérifie si les infos liées au token sont les mêmes que celles de la base de données,
         et crée l'objet authentication si c'est le cas */
        UserDetails userDetails = getUserFromDatabase(tokenInfo.username);

        if (jwtService.validateToken(tokenInfo.token, userDetails)){
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);;
        }
    }
    
    private UserDetails getUserFromDatabase(String username){
        return context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
    }
    
}
