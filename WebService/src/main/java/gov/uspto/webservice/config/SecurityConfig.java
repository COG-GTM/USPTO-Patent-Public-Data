package gov.uspto.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for USPTO Patent Web Service.
 * 
 * Uses Spring Security 6.x SecurityFilterChain pattern for modern configuration.
 * Configures CSRF, CORS, session management, and authentication.
 * 
 * NOTE: AuthenticationService integration is pending Part 1.9 completion.
 * Once available, this configuration will be updated to integrate with the
 * AuthenticationService module for JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain with modern Spring Security 6.x pattern.
     * 
     * Current configuration:
     * - Permits all requests (temporary until AuthenticationService is integrated)
     * - CSRF disabled for REST API
     * - CORS enabled via WebConfig
     * - Stateless session management for REST API
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .cors(cors -> cors.configure(http))
            
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * BCrypt password encoder for secure password hashing.
     * 
     * Will be used by AuthenticationService once integrated.
     * BCrypt is recommended by NIST 800-53 IA-5 for password storage.
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
