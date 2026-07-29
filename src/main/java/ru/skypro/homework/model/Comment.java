package ru.skypro.homework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.homework.model.user.User;

/**
 * Сущность комментария к объявлению.
 * <p>
 * Содержит текст комментария, дату создания, автора и ссылку на объявление,
 * к которому оставлен комментарий.
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Текст комментария (обязательный). */
    @Column(name = "text", nullable = false)
    private String text;

    /** Дата создания комментария в миллисекундах (устанавливается автоматически). */
    @Column(name = "created_at")
    private Long createdAt = System.currentTimeMillis();

    /** Автор комментария. */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    /** Объявление, к которому оставлен комментарий. */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Ad ad;
}
