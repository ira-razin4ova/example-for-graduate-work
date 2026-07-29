package ru.skypro.homework.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр CORS для Basic-аутентификации.
 * <p>
 * Добавляет заголовок {@code Access-Control-Allow-Credentials: true}
 * к каждому HTTP-ответу, чтобы браузер разрешал передачу credentials
 * (например, заголовка Authorization) при кросс-доменных запросах.
 * </p>
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Добавляет заголовок CORS и передаёт запрос дальше по цепочке фильтров.
     *
     * @param httpServletRequest  входящий запрос
     * @param httpServletResponse исходящий ответ
     * @param filterChain         цепочка фильтров
     * @throws ServletException если возникла ошибка сервлета
     * @throws IOException      если возникла ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
