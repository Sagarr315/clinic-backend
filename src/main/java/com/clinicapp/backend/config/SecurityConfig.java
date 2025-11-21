package com.clinicapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                // Public endpoints - Anyone can view clinics
                .requestMatchers("/api/auth/**", "/api/hello", "/api/superadmin/**", "/api/clinics/public", "/api/clinics/subdomain/**")  //  Keep clinic public
                .permitAll()

                //  ADMIN endpoints - ONLY ROLE_ADMIN_DOCTOR
                .requestMatchers("/api/admin/**").hasRole("ADMIN_DOCTOR")

                // ️ DOCTOR endpoints
                .requestMatchers("/api/doctors/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR", "RECEPTIONIST")

                //  RECEPTIONIST endpoints
                .requestMatchers("/api/receptionists/**").hasRole("RECEPTIONIST")

                //  Appointment booking - ONLY RECEPTIONISTS
                .requestMatchers("/api/appointments/book").hasRole("RECEPTIONIST")

                //  Appointment management
                .requestMatchers("/api/appointments/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR", "RECEPTIONIST")

                //  Analytics
                .requestMatchers("/api/analytics/summary").hasRole("ADMIN_DOCTOR").requestMatchers("/api/analytics/doctor/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR")

                //  Prescriptions
                .requestMatchers("/api/prescriptions/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR", "RECEPTIONIST")

                // BILLING endpoints - RECEPTIONIST & ADMIN ONLY (NO DOCTOR)
                .requestMatchers("/api/bills/**").hasAnyRole("ADMIN_DOCTOR", "RECEPTIONIST")


                // PATIENT endpoints - FIXED: Add both patterns
                .requestMatchers("/api/patients/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR", "RECEPTIONIST")


                .requestMatchers("/api/doctor-schedules/**").hasAnyRole("ADMIN_DOCTOR", "DOCTOR", "RECEPTIONIST")
                // All other endpoints need authentication
                .anyRequest().authenticated()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}