package com.vetopia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired 
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/h2/**").permitAll()
                .requestMatchers("/dueno/login").permitAll()
                .requestMatchers("/veterinario/login").permitAll()
                .requestMatchers("/dueno/details").authenticated()
                .requestMatchers("/veterinario/details").authenticated()
                .requestMatchers("/auth/validate").authenticated()
                .requestMatchers(HttpMethod.POST, "/dueno").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.PUT, "/dueno/**").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.DELETE, "/dueno/**").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.POST, "/mascota").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.PUT, "/mascota/**").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.DELETE, "/mascota/**").access(conTokenYRol("VETERINARIO"))
                .requestMatchers(HttpMethod.POST, "/veterinario").access(conTokenYRol("ADMIN"))
                .requestMatchers("/mascota/**").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint));

            http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(){
        return new JWTAuthenticationFilter();
    }

    private AuthorizationManager<RequestAuthorizationContext> conTokenYRol(String rol) {
        return AuthorizationManagers.allOf(
            AuthenticatedAuthorizationManager.authenticated(),
            AuthorityAuthorizationManager.hasAuthority(rol)
        );
    }
}
