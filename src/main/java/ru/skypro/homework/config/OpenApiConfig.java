package ru.skypro.homework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;

/**
 * Конфигурация OpenAPI (Swagger) документации.
 * <p>
 * Настраивает метаданные API (название, версию, описание)
 * и схему безопасности HTTP Basic Auth для всех endpoints.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создаёт кастомную конфигурацию OpenAPI.
     * <p>
     * Регистрирует схему безопасности {@code basicAuth} (HTTP Basic),
     * применяет её глобально ко всем endpoints и задаёт информацию о документе.
     * </p>
     *
     * @return объект {@link OpenAPI} с конфигурацией документации
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .info(new Info()
                        .title("Ads API")
                        .version("1.0")
                        .description("API для доски объявлений"));
    }
}

