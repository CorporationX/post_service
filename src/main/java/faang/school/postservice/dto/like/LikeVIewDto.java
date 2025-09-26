package faang.school.postservice.dto.like;

import java.time.LocalDateTime;

/**
 * LikeVIewDto — DTO для передачи данных о лайке.
 *
 * @param likeAuthorId - ID пользователя который поставил лайк
 * @param postId - ID поста на который поставили лайк
 * @param createdAt - время постановки лайка
 * @author andreyfomchenko
 * @since 26.09.2025
 */
public record LikeVIewDto(
        Long likeAuthorId,
        Long postId,
        LocalDateTime createdAt
) {
}