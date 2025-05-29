package faang.school.postservice.model;

import faang.school.postservice.model.ad.Ad;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Класс `Post` представляет собой сущность, моделирующую пост в социальной сети.
 * Пост может содержать текстовое содержимое, а также быть связан с лайками, комментариями,
 * альбомами, рекламными объявлениями и ресурсами (изображения, видео и т.д.).
 * Пост может быть опубликован, запланирован на публикацию или помечен как удаленный.
 *
 * <p>Каждый пост имеет уникальный идентификатор, автора, содержимое и метаданные,
 * такие как дата создания, обновления и публикации.</p>
 *
 * <p>Связанные сущности:
 * <ul>
 *     <li>Лайки ({@link Like})</li>
 *     <li>Комментарии ({@link Comment})</li>
 *     <li>Альбомы ({@link Album})</li>
 *     <li>Рекламные объявления ({@link Ad})</li>
 *     <li>Ресурсы ({@link Resource})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh, murloc_teams
 * @version 1.0
 * @since 2025-17-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    /**
     * Уникальный идентификатор поста. Генерируется автоматически при создании записи в базе данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Содержимое поста. Максимальная длина — 4096 символов. Поле не может быть пустым.
     */
    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    /**
     * Идентификатор автора поста.
     */
    @Column(name = "author_id")
    private Long authorId;

    /**
     * Идентификатор проекта, к которому относится пост. Может быть пустым.
     */
    @Column(name = "project_id")
    private Long projectId;

    /**
     * Список лайков, поставленных под этим постом.
     * При удалении поста все связанные лайки также удаляются.
     */
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Like> likes;

    /**
     * Список комментариев к этому посту.
     * При удалении поста все связанные комментарии также удаляются.
     */
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments;

    /**
     * Список альбомов, в которые включен этот пост.
     */
    @ManyToMany(mappedBy = "posts")
    private List<Album> albums;

    /**
     * Рекламное объявление, связанное с этим постом.
     * При удалении поста связанное рекламное объявление также удаляется.
     */
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private Ad ad;

    /**
     * Список ресурсов (изображений, видео и т.д.), связанных с этим постом.
     * При удалении поста все связанные ресурсы также удаляются.
     */
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Resource> resources;

    /**
     * Флаг, указывающий, опубликован ли пост. По умолчанию `false`.
     */
    @Column(name = "published", nullable = false)
    private boolean published;

    /**
     * Дата и время публикации поста. Может быть пустым, если пост еще не опубликован.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * Дата и время, когда пост должен быть опубликован (если запланирован). Может быть пустым.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /**
     * Флаг, указывающий, удален ли пост. По умолчанию `false`.
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    /**
     * Дата и время создания поста. Заполняется автоматически при создании записи.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления поста. Заполняется автоматически при обновлении записи.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Флаг, указывающий, проверен ли пост модератором..
     */
    @Column(name = "verified")
    private boolean verified;

    /**
     * Дата и время проверки поста модератором. Может быть пустым, если пост не проверен.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
