package faang.school.postservice.dto.post;

/**
 * Класс-ивент для уведомления о публикации поста
 *
 * @author Linempy
 * @since 23.08.2025
 */
public record PostPublishedEvent(
    Long postId,
    Long authorId,
    Long projectId
) {
}