package ru.skypro.homework.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность пользователя.
 * <p>
 * Хранит учетные данные, персональную информацию и роль пользователя в системе.
 * Каждый пользователь идентифицируется уникальным email и имеет пароль для аутентификации.
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Email пользователя (уникальный, обязательный). Используется в качестве логина. */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /** Хэш пароля пользователя (обязательный). */
    @Column(name = "password", nullable = false)
    private String password;

    /** Имя пользователя (обязательное). */
    @Column (name = "first_name", nullable = false)
    private String firstName;

    /** Фамилия пользователя (обязательная). */
    @Column (name = "last_name", nullable = false)
    private String lastName;

    /** Номер телефона пользователя (обязательный). */
    @Column (name = "phone", nullable = false)
    private String phone;

    /** Роль пользователя в системе ({@link Role#USER} или {@link Role#ADMIN}). */
    @Column (name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Путь к аватару пользователя (необязательный). */
    @Column(name = "image")
    private String image;
}
