package faang.school.postservice.dto.redis;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.PostFeedDto;

/**
 * DTO для сохранения в Redis
 * Переиспользуется в {@link PostFeedDto} и {@link CommentFeedDto}
 *
 * @param id идентификатор пользователя
 * @param username имя пользователя
 *
 * @author Linempy
 * @since 27.09.2025
 */
public record UserRedisDto(
        Long id,
        String username
) {
}