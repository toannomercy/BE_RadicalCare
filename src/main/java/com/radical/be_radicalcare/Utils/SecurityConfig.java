package com.radical.be_radicalcare.Utils;

import com.radical.be_radicalcare.Services.OAuthService;
import com.radical.be_radicalcare.Services.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final Dotenv dotenv = Dotenv.load();

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        String clientId = dotenv.get("OAUTH2_GOOGLE_CLIENT_ID");
        String clientSecret = dotenv.get("OAUTH2_GOOGLE_CLIENT_SECRET");

        ClientRegistration googleClientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .redirectUri("http://192.168.1.33:8080/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        return new InMemoryClientRegistrationRepository(googleClientRegistration);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reset-password/shown").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAnyAuthority("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(withDefaults())
                .oauth2Login(withDefaults())
                .rememberMe(rememberMe -> rememberMe
                        .key("radical")
                        .rememberMeCookieName("radical")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedPage("/403")
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.maximumSessions(1).expiredUrl("/login")
                )
                .httpBasic(httpBasic ->
                        httpBasic.realmName("radical")
                )
                .build();
    }
}