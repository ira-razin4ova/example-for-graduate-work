package ru.skypro.homework;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Главный класс приложения для управления объявлениями.
 * <p>
 * Точка входа в Spring Boot приложение. Запускает встроенный веб-сервер
 * и инициализирует контекст Spring с OpenAPI-документацией.
 * </p>
 */
@SpringBootApplication
@OpenAPIDefinition
public class HomeworkApplication {
  /**
   * Точка входа в приложение.
   *
   * @param args аргументы командной строки
   */
  public static void main(String[] args) {
    SpringApplication.run(HomeworkApplication.class, args);
  }
}
