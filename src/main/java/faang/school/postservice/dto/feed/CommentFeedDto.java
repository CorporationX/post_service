package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.UserRedisDto;

import java.time.LocalDateTime;

/**
 * DTO для использования сущности комментария в ленте новостей
 *
 * @author Linempy
 * @since 28.09.2025
 */
public record CommentFeedDto(
    Long id,
    UserRedisDto author,
    String content,
    LocalDateTime createdAt
) {
}