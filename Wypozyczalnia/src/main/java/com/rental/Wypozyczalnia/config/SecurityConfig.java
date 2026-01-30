package com.rental.Wypozyczalnia.config;

import com.rental.Wypozyczalnia.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers("/role/**").hasAnyRole("Dzial_HR", "test")
                        .requestMatchers("/user/**").hasAnyRole("Dzial_HR", "test")


                        .requestMatchers("/wypozyczalnie/**").hasAnyRole("Zarzad", "test")


                        .requestMatchers("/klienci/**").access((authentication, context) ->
                                new org.springframework.security.authorization.AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .noneMatch(grantedAuth -> grantedAuth.getAuthority().equals("ROLE_Dzial_HR"))
                                ))
                        .requestMatchers("/wypozyczalnia/**").access((authentication, context) ->
                                new org.springframework.security.authorization.AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .noneMatch(grantedAuth -> grantedAuth.getAuthority().equals("ROLE_Dzial_HR"))
                                ))
                        .requestMatchers("/samochody/**").access((authentication, context) ->
                                new org.springframework.security.authorization.AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .noneMatch(grantedAuth -> grantedAuth.getAuthority().equals("ROLE_Dzial_HR"))
                                ))

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/403")
                )
                .formLogin(form -> form
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // Custom redirect based on role
                            String role = authentication.getAuthorities().stream()
                                    .findFirst()
                                    .map(auth -> auth.getAuthority())
                                    .orElse("ROLE_USER");

                            String redirectUrl = switch (role) {
                                case "ROLE_Dzial_HR" -> "/user";
                                case "ROLE_Zarzad" -> "/wypozyczalnie";
                                default -> "/klienci";
                            };

                            response.sendRedirect(redirectUrl);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}