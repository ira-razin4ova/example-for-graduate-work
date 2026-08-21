package ru.skypro.homework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация CORS для приложения.
 * <p>
 * Разрешает кросс-доменные запросы с фронтенда ({@code http://localhost:3000}).
 * Поддерживаются все стандартные HTTP-методы и передача учётных данных (cookies/headers).
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настраивает политику CORS для всех эндпоинтов приложения.
     *
     * @param registry реестр правил CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
