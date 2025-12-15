package com.eva.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity //replace default security config 
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

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
        return  http.csrf(customizer -> customizer.disable()) //désactive csrf par exemple s'il 
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/register", "/auth/login", "/auth/logout", "/auth/refresh").permitAll() //ces url sont accessibles à tous
                        .requestMatchers("/auth/users").hasRole("ADMIN")
                        .anyRequest().authenticated()) //filtre exigeant l'authentification pour tout requête
                    .httpBasic(Customizer.withDefaults()) // popup obligeant à s'authentifier
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Détruit la session à chaque requête
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }

    /* Gestion des url qui peuvent effectuer des requêtes à l'api */
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // ← Nécessaire pour JWT/Auth
        configuration.setExposedHeaders(List.of("Set-Cookie")); // permet au frontend de voir les cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
