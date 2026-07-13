package ru.skypro.homework;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@OpenAPIDefinition
@EnableJpaRepositories("ru.skypro.homework.repository")
public class HomeworkApplication {
  public static void main(String[] args) {
    SpringApplication.run(HomeworkApplication.class, args);
  }
}
