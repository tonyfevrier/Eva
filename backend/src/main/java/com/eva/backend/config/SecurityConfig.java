package com.eva.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //replace default security config 
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /* Par défaut spring security applique une série de filtres en cas de requête
    il exige un login par exemple. On peut remplacer cette série et customizer ces filtres
    grâce à cette classe SecurityConfig.
     */

    /*Configuration avec session id créé et cookies permettant de conserver
     * la session ouverte.
     */
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(Customizer.withDefaults())
                   .authorizeHttpRequests(request -> request.anyRequest().authenticated()) //filtre exigeant l'authentification pour tout requête
                   .formLogin(Customizer.withDefaults()) // pour exiger le login et générer un session id à chaque login, cet id permet de conserver la session ouverte si on la rafraîchit (paramètres mis dans les cookies)
                   .logout(Customizer.withDefaults())
                   .build();
    }*/

    /* Config qui ne nécessite pas de csrf car il n'y a pas de session, à chaque requête
     * la session est détruite donc pas de cookies et de risque qu'un site malveillant récupère les données.
        C'est ce qu'on utilise pour une API REST     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(customizer -> customizer.disable()) //désactive csrf par exemple s'il 
                   .authorizeHttpRequests(request -> request.anyRequest().authenticated()) //filtre exigeant l'authentification pour tout requête
                   .httpBasic(Customizer.withDefaults()) // popup obligeant à s'authentifier
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Détruit la session à chaque requête
                   .build();
    }

    /* Il faut aussi fournir un fournisseur d'authentication qui va traiter les requêtes 
     * d'authentification. Il y en a un par défaut mais on en veut un basé sur une bdd.
     * Celui-ci nécessite un service pour gérer les données user: 
     * UserDetailService, par défaut spring boot en contient un mais on peut lui fournir le notre
     */
    
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12)); // 
        return provider;
    }

}
