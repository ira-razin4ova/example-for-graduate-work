package ru.skypro.homework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.homework.model.user.User;

/**
 * Сущность объявления.
 * <p>
 * Содержит информацию о товаре/услуге: заголовок, описание, цену, изображение и автора.
 * Каждое объявление привязано к конкретному пользователю ({@link User}).
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Описание объявления (необязательное). */
    @Column(name = "description")
    private String description;

    /** Путь к изображению объявления (необязательное). */
    @Column(name = "image")
    private String image;

    /** Цена в условных единицах (обязательная). */
    @Column(name = "price", nullable = false)
    private Integer price;

    /** Заголовок объявления (обязательный). */
    @Column(name = "title", nullable = false)
    private String title;

    /** Автор объявления. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}
