package ru.skypro.homework.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Конфигурация безопасности Spring Security.
 * <p>
 * Настраивает HTTP Basic аутентификацию, правила доступа к endpoints,
 * открывает доступ к Swagger-документации, отключает CSRF, настраивает CORS
 * и обработку ошибок аутентификации/авторизации.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register",
            "/error"
    };

    /**
     * Настраивает цепочку фильтров безопасности.
     *
     * @param http объект конфигурации HTTP безопасности
     * @return настроенная цепочка фильтров
     * @throws Exception если возникла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/ads/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/ads/**", "/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/ads", "/ads/**", "/comments/**", "/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ads/**", "/comments/**", "/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/ads/**", "/comments/**").hasAnyRole("USER", "ADMIN")
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Создаёт бин для кодирования паролей через BCrypt.
     *
     * @return {@link PasswordEncoder} с алгоритмом BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт бин для аутентификации.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return {@link AuthenticationManager}
     * @throws Exception если возникла ошибка
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
