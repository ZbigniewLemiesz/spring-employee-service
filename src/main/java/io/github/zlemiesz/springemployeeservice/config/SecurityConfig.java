package io.github.zlemiesz.springemployeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationEntryPoint restEntryPoint,
                                            AccessDeniedHandler restDeniedHandler) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout").permitAll()

                        // READ: VIEWER, MANAGER, HR, ADMIN
                        .requestMatchers(HttpMethod.GET, "/employee/**")
                        .hasAnyRole("VIEWER", "MANAGER", "HR", "ADMIN")

                        // CREATE: HR, ADMIN
                        .requestMatchers(HttpMethod.POST, "/employee/**")
                        .hasAnyRole("HR", "ADMIN")

                        // UPDATE: MANAGER, HR, ADMIN
                        .requestMatchers(HttpMethod.PUT, "/employee/**")
                        .hasAnyRole("MANAGER", "HR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/employee/**")
                        .hasAnyRole("MANAGER", "HR", "ADMIN")

                        // DELETE: ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/employee/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                // formularz logowania
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.NO_CONTENT.value()); // 204
                        })
                        .failureHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
                        })
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.NO_CONTENT.value()); // 204
                        })
                )

                // jeżeli API ma zwracać 401/403 w JSON (ProblemDetail) zamiast redirectów:
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restEntryPoint)
                        .accessDeniedHandler(restDeniedHandler)
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "X-XSRF-TOKEN"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


}

